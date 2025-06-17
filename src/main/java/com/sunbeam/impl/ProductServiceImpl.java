package com.sunbeam.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.product.ProductDTO;
import com.sunbeam.models.Product;
import com.sunbeam.product.ProductNotFound;
import com.sunbeam.repository.ProductRepo;
import com.sunbeam.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo repo;

	@Override
	public Product addProduct(ProductDTO product) {
		log.info("Adding new product: {}", product);
		Product newProduct = Product.builder().name(product.getName()).price(product.getPrice())
				.description(product.getDescription()).build();

		Product savedProduct = repo.save(newProduct);
		log.info("Product saved successfully: {}", savedProduct);
		return savedProduct;
	}

	@Override
	public Product getProductById(Long id) {
		log.info("Fetching product with ID: {}", id);
		Optional<Product> product = repo.findById(id);

		if (product.isEmpty()) {
			log.error("Product with ID {} not found", id);
			throw new ProductNotFound("No product with this ID: " + id);
		}

//		log.info("Product found: {}", product.get());
		return product.get();
	}

	@Override
	public Page<Product> getProducts(int page, int size, String orderBy, String direction) {
		log.info("Fetching products with page: {}, size: {}, orderBy: {}, direction: {}", page, size, orderBy,
				direction);

		// Convert negative page number to positive
		if (page < 1) {
			page = 1;
		}

		// Determine sort direction (default to ASC)
		Sort.Direction sortDirection = Sort.Direction.ASC;
		if ("desc".equalsIgnoreCase(direction)) {
			sortDirection = Sort.Direction.DESC;
		}

		// Create pageable with sorting
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, orderBy));
		Page<Product> products = repo.findAll(pageable);

		// Handle invalid page number
		if (page > products.getTotalPages() && products.getTotalPages() > 0) {
			pageable = PageRequest.of(products.getTotalPages() - 1, size, Sort.by(sortDirection, orderBy));
			products = repo.findAll(pageable);
		}

		log.info("Products fetched successfully: {} records found", products.getTotalElements());
		return products;
	}

	@Override
	public boolean deleteProductById(Long id) {
		log.info("fetching product with id: " + id);
		Optional<Product> product = repo.findById(id);
		if (product.isEmpty()) {
			log.error("Product with ID {} not found", id);
			throw new ProductNotFound("No product with this ID: " + id);
		}
		repo.deleteById(id);
		log.info("product deleted with id: " + id);
		return true;
	}

	@Override
	public Product updateProductById(Long id, ProductDTO productDTO) {
		log.info("Fetching product with ID: {}", id);
		Product existingProduct = repo.findById(id).orElseThrow(() -> new ProductNotFound("No product with ID: " + id));

		if (productDTO.getName() != null) {
			existingProduct.setName(productDTO.getName());
		}
		if (productDTO.getPrice() != null) {
			existingProduct.setPrice(productDTO.getPrice());
		}
		if (productDTO.getDescription() != null) {
			existingProduct.setDescription(productDTO.getDescription());
		}
//	    if (productDTO.getStock() != null) {
//	        existingProduct.setStock(productDTO.getStock());
//	    }

		Product updatedProduct = repo.save(existingProduct);
		log.info("Updated product with ID: {}", id);

		return updatedProduct;
	}

}
