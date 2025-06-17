package com.sunbeam.dto.product;

import java.time.Instant;
import java.util.List;

import com.sunbeam.models.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
	private Long productId;
	private String name;
	private String description;
	private Double price;
	private Instant updatedAt;
	private Instant createdAt;
	private List<String> images;

	public ProductDTO(Product product) {
		this.productId = product.getId();
		this.name = product.getName();
		this.images = product.getImageUrls();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.createdAt = product.getCreatedAt();
		this.updatedAt = product.getUpdatedAt();
	}

}
