package com.sunbeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunbeam.models.Product;
import com.sunbeam.models.ProductImage;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {
	void deleteByProductAndImageUrl(Product product, String imageUrl);

	void deleteByProduct(Product product);
}
