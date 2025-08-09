// src/main/java/com/sunbeam/controllers/InventoryController.java
package com.sunbeam.controllers;

import com.sunbeam.dto.inventory.InventoryDTO;
import com.sunbeam.dto.inventory.InventoryUpsertRequest;
import com.sunbeam.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Page<InventoryDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort
    ) {
        String[] parts = sort.split(",", 2);
        Sort sortObj = (parts.length == 2 && "asc".equalsIgnoreCase(parts[1]))
                ? Sort.by(parts[0]).ascending()
                : Sort.by(parts[0]).descending();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sortObj);
        return inventoryService.list(pageable);
    }

    @GetMapping("/{id}")
    public InventoryDTO get(@PathVariable Long id) {
        return inventoryService.get(id);
    }

    @PostMapping
    public InventoryDTO create(@Valid @RequestBody InventoryUpsertRequest req) {
        return inventoryService.create(req);
    }

    @PutMapping("/{id}")
    public InventoryDTO update(@PathVariable Long id, @Valid @RequestBody InventoryUpsertRequest req) {
        return inventoryService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
