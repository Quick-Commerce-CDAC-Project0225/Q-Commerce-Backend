package com.sunbeam.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.product.ProductDTO;
import com.sunbeam.models.Product;

@Service
public interface ProductService {
	Product addProduct(ProductDTO product);

	Product getProductById(Long id);

	Page<Product> getProducts(int page, int size, String orderBy, String direction);

	boolean deleteProductById(Long id);

	Product updateProductById(Long id, ProductDTO product);

}
