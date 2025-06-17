package com.sunbeam.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunbeam.dto.ResponseDTO;
import com.sunbeam.dto.image.ImageUploadResultDTO;
import com.sunbeam.service.ImageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/images")
@Slf4j
@Lazy
public class ImageController {
	private static final String IMAGE_DIR = "src/main/resources/static/images/";

	private final ImageService imageService;

	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/upload/{productId}")
	public ResponseEntity<ResponseDTO<List<ImageUploadResultDTO>>> uploadImage(@RequestParam MultipartFile[] files,
			@PathVariable Long productId) {
		log.info("Uploading image for product ID: {}", productId);
		try {
			List<ImageUploadResultDTO> imageUrls = imageService.uploadImages(files, productId);
			ResponseDTO<List<ImageUploadResultDTO>> response = new ResponseDTO<>(true, "Image uploaded successfully",
					imageUrls);

			log.info("Image uploaded successfully: {}", imageUrls);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IOException e) {
			log.error("Image upload failed due to IO exception: {}", e.getMessage());
			return ResponseEntity.badRequest().body(new ResponseDTO<>(false, "Image upload failed", null));
		} catch (Exception e) {
			log.error("Unexpected error during image upload: {}", e.getMessage());
			return ResponseEntity.badRequest().body(new ResponseDTO<>(false, "Image upload failed", null));
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{productId}/{fileName}")
	public ResponseEntity<ResponseDTO<Void>> deleteImage(@PathVariable Long productId, @PathVariable String fileName) {
		log.info("Deleting image '{}' for product ID: {}", fileName, productId);
		boolean success = imageService.deleteImage(productId, fileName);
		if (success) {
			return ResponseEntity.ok(new ResponseDTO<>(true, "Image moved to deleted folder", null));
		} else {
			return ResponseEntity.badRequest().body(new ResponseDTO<>(false, "Image not found", null));
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/all/{productId}")
	public ResponseEntity<ResponseDTO<Void>> deleteAllImages(@PathVariable Long productId) {
		log.info("Deleting all images for product ID: {}", productId);
		boolean success = imageService.deleteAllImages(productId);
		if (success) {
			return ResponseEntity.ok(new ResponseDTO<>(true, "All images moved to deleted folder", null));
		} else {
			return ResponseEntity.badRequest().body(new ResponseDTO<>(false, "No images found", null));
		}

	}

	@GetMapping("/{folder}/{filename:.+}")
	public ResponseEntity<Resource> getImage(@PathVariable String folder, @PathVariable String filename)
			throws IOException {
		Path imagePath = Paths.get(IMAGE_DIR + folder + "/" + filename);
		if (!Files.exists(imagePath)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new UrlResource(imagePath.toUri());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).header("Cache-Control", "public, max-age=31536000") // Cache
																															// for
																															// 1
																															// year
				.header("ETag", String.valueOf(Files.getLastModifiedTime(imagePath).toMillis())).body(resource);

	}
}
