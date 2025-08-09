package com.sunbeam.controllers;


import com.sunbeam.dto.store.StoreDTO;

import com.sunbeam.service.WarehouseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class WarehouseController {

    private final WarehouseService storeService;

    // 1. GET all stores
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    // 2. POST a new store
    @PostMapping
    public ResponseEntity<StoreDTO> addStore(@RequestBody StoreDTO storeDTO) {
        StoreDTO savedStore = storeService.addStore(storeDTO);
        return ResponseEntity.ok(savedStore);
    }

    // 3. PUT to toggle store status (active/inactive)
    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleStoreStatus(@PathVariable Long id) {
        boolean updated = storeService.toggleStoreStatus(id);
        return updated
            ? ResponseEntity.ok("Store status updated.")
            : ResponseEntity.badRequest().body("Failed to update status.");
    }

 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStore(@PathVariable Long id) {
        storeService.deleteStoreById(id);
        return ResponseEntity.ok("Store deleted successfully.");
    }
}
