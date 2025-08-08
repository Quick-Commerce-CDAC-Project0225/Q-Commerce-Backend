package com.sunbeam.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.inventory.InventoryDTO;
import com.sunbeam.dto.inventory.InventoryUpsertRequest;
import com.sunbeam.exceptions.ResourceNotFoundException;
import com.sunbeam.models.Inventory;
import com.sunbeam.models.Product;
import com.sunbeam.models.Warehouse;
import com.sunbeam.repository.InventoryRepository;
import com.sunbeam.repository.ProductRepo;
import com.sunbeam.repository.WarehouseRepository;
import com.sunbeam.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepo productRepo;
    private final WarehouseRepository warehouseRepo;

    @Override
    public Page<InventoryDTO> list(Pageable pageable) {
        return inventoryRepo.findAll(pageable).map(InventoryDTO::from);
    }

    @Override
    public InventoryDTO get(Long id) {
        var inv = inventoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
        return InventoryDTO.from(inv);
    }

    @Override
    public InventoryDTO create(InventoryUpsertRequest req) {
        Product product = productRepo.findById(req.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + req.productId()));
        Warehouse warehouse = warehouseRepo.findById(req.warehouseId())
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + req.warehouseId()));

        Inventory inv = new Inventory();
        inv.setSku(req.sku());
        inv.setProduct(product);
        inv.setWarehouse(warehouse);
        return InventoryDTO.from(inventoryRepo.save(inv));
    }

    @Override
    public InventoryDTO update(Long id, InventoryUpsertRequest req) {
        Inventory inv = inventoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));

        if (req.sku() != null) inv.setSku(req.sku());
        if (req.productId() != null) {
            Product p = productRepo.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + req.productId()));
            inv.setProduct(p);
        }
        if (req.warehouseId() != null) {
            Warehouse w = warehouseRepo.findById(req.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + req.warehouseId()));
            inv.setWarehouse(w);
        }
        return InventoryDTO.from(inventoryRepo.save(inv));
    }

    @Override
    public void delete(Long id) {
        if (!inventoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found: " + id);
        }
        inventoryRepo.deleteById(id);
    }
}
