package com.sunbeam.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunbeam.dto.image.ImageUploadResultDTO;
import com.sunbeam.models.Product;
import com.sunbeam.models.ProductImage;
import com.sunbeam.repository.ProductImageRepo;
import com.sunbeam.repository.ProductRepo;
import com.sunbeam.service.ImageService;

import jakarta.transaction.Transactional;

@Lazy
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

	private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
	private static final String IMAGE_DIR = "src/main/resources/static/images/";
	private static final String DELETED_DIR = "src/main/resources/static/deleted/";
	private static final int MAX_SIZE = 2 * 1024 * 1024; // 2MB limit

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private ProductImageRepo productImageRepo;

	@Override
	public List<ImageUploadResultDTO> uploadImages(MultipartFile[] files, Long productId) {
		log.info("Uploading images for product ID: {}", productId);
		List<String> imageUrls = new ArrayList<>();

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		try {
			Path directory = Paths.get(IMAGE_DIR, String.valueOf(productId));
			Files.createDirectories(directory);
		} catch (IOException e) {
			log.error("Failed to create image directory for product {}: {}", productId, e.getMessage(), e);
			throw new RuntimeException("Could not create image directory", e);
		}

		List<CompletableFuture<ImageUploadResultDTO>> futures = Arrays.stream(files)
				.map(file -> CompletableFuture.supplyAsync(() -> processImage(file, productId, product)))
				.collect(Collectors.toList());

		List<ImageUploadResultDTO> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		log.info("Total {} images uploaded successfully.", imageUrls.size());
		return results;
	}

	private String formatSize(long bytes) {
		if (bytes >= 1024 * 1024) {
			return String.format("%.2f MB", bytes / (1024.0 * 1024));
		}
		if (bytes >= 1024) {
			return String.format("%.2f KB", bytes / 1024.0);
		}
		return bytes + " bytes";
	}

	private ImageUploadResultDTO processImage(MultipartFile file, Long productId, Product product) {
		String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown");
		ImageUploadResultDTO resultDTO = new ImageUploadResultDTO();

		if (file.isEmpty()) {
			log.warn("Skipped empty file.");
			resultDTO.uploadError("File is empty.");
			resultDTO.setFileName(originalFilename);
			return resultDTO;
		}

		if (!originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|webp)$")) {
			log.warn("Invalid image format: {}", originalFilename);
			resultDTO.uploadError("Invalid image format. Allowed: jpg, jpeg, png, webp.");
			resultDTO.setFileName(originalFilename);
			return resultDTO;
		}

		if (file.getSize() > MAX_SIZE) {
			log.warn("File too large: {} bytes (Max: 2MB)", file.getSize());
			resultDTO.uploadError("File too large. Max size allowed is 2MB.");
			resultDTO.setFileName(originalFilename);
			resultDTO.setSizeInBytes(file.getSize());
			return resultDTO;
		}

		String fileName = UUID.randomUUID() + ".jpeg";
		Path filePath = Paths.get(IMAGE_DIR, String.valueOf(productId), fileName);

		long startTime = System.currentTimeMillis();
		try {
			byte[] imageBytes = file.getBytes();
			long originalSize = imageBytes.length;
			log.info("📷 Original image size: {}", formatSize(originalSize));

			if (!originalFilename.toLowerCase().endsWith(".jpeg")) {
				log.info("Converting {} to JPEG format", originalFilename);
				imageBytes = convertToJpeg(file);
			}

			Files.copy(new ByteArrayInputStream(imageBytes), filePath, StandardCopyOption.REPLACE_EXISTING);
			long savedSize = Files.size(filePath);
			log.info("✅ Image '{}' uploaded successfully. Final size: {}", fileName, formatSize(savedSize));

			String imageUrl = "/images/" + productId + "/" + fileName;
			productImageRepo.save(ProductImage.builder().imageUrl(imageUrl).product(product).build());

			long endTime = System.currentTimeMillis();
			log.info("⏱️ Image '{}' uploaded in {} ms.", fileName, (endTime - startTime));

			resultDTO.uploadSuccess(fileName, imageUrl, savedSize);
			return resultDTO;

		} catch (IOException e) {
			log.error("❌ Failed to process image {}: {}", fileName, e.getMessage(), e);
			resultDTO.uploadError("Failed to process image: " + e.getMessage());
			resultDTO.setFileName(originalFilename);
			return resultDTO;
		}
	}

	private byte[] convertToJpeg(MultipartFile file) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(newImage, "jpeg", outputStream);
		return outputStream.toByteArray();
	}

	@Override
	public boolean deleteImage(Long productId, String fileName) {
		log.info("Deleting image '{}' for product ID: {}", fileName, productId);

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		Path imagePath = Paths.get(IMAGE_DIR, String.valueOf(productId), fileName);
		Path deletedPath = Paths.get(DELETED_DIR, String.valueOf(productId), fileName);

		try {
			boolean flag = Files.deleteIfExists(imagePath);
			if (flag == false) {
				log.warn("⚠️ File does not exist, so it cannot be deleted: {}", imagePath);
				return false;
			}
			// move to deleted DIR START
//			Files.createDirectories(deletedPath.getParent());
//			Files.move(imagePath, deletedPath, StandardCopyOption.REPLACE_EXISTING);
			// move to deleted DIR END
			log.info("✅ Image moved to deleted folder: {}", deletedPath);
			String fullImageName = "/images/" + productId + "/" + fileName;
			log.warn(fullImageName);
			productImageRepo.deleteByProductAndImageUrl(product, fullImageName);
			log.warn(fullImageName);
			return true;
		} catch (Exception e) {
			log.error("❌ Failed to delete image '{}': {}", fileName, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean deleteAllImages(Long productId) {
		log.info("Deleting all images for product ID: {}", productId);

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		Path productFolder = Paths.get(IMAGE_DIR, String.valueOf(productId));
		Path deletedFolder = Paths.get(DELETED_DIR, String.valueOf(productId));

		if (!Files.exists(productFolder)) {
			log.warn("No images found for product ID: {}", productId);
			return false;
		}

		try {
			Files.createDirectories(deletedFolder);
			Files.list(productFolder).forEach(file -> {
				try {
					Files.move(file, deletedFolder.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					log.info("✅ Image '{}' moved to deleted folder", file.getFileName());
				} catch (IOException e) {
					log.error("❌ Failed to move image '{}': {}", file.getFileName(), e.getMessage(), e);
				}
			});
			productImageRepo.deleteByProduct(product);
			return true;
		} catch (IOException e) {
			log.error("❌ Error deleting images: {}", e.getMessage(), e);
			return false;
		}
	}
}
