package com.sunbeam.service;

import com.sunbeam.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product addProduct(com.sunbeam.dto.product.ProductDTO dto);
    Product getProductById(Long id);
    Product updateProductById(Long id, com.sunbeam.dto.product.ProductDTO dto);
    boolean deleteProductById(Long id);

    Page<Product> getProducts(int page, int size, String sortProp, String sortDir);

    // Make sure this returns a non-null List
    List<Product> getAllProducts();
}

