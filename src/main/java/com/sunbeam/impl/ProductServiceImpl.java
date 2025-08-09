package com.sunbeam.impl;

import com.sunbeam.dto.product.ProductDTO;
import com.sunbeam.models.Product;
import com.sunbeam.repository.ProductRepo;
import com.sunbeam.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    @Override
    public Product addProduct(ProductDTO dto) {
        Product p = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
        return productRepo.save(p);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Override
    public Product updateProductById(Long id, ProductDTO dto) {
        Product p = getProductById(id);
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        return productRepo.save(p);
    }

    @Override
    public boolean deleteProductById(Long id) {
        if (!productRepo.existsById(id)) return false;
        productRepo.deleteById(id);
        return true;
    }

    @Override
    public Page<Product> getProducts(int page, int size, String sortProp, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortProp).descending()
                : Sort.by(sortProp).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        return productRepo.findAll(pageable);
    }

    @Override
    public List<Product> getAllProducts() {
        // <-- NEVER return null; return an empty list at worst.
        return productRepo.findAll();
    }
}

