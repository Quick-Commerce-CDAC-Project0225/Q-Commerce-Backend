package com.sunbeam.service;

import java.util.List;

import com.sunbeam.dto.store.StoreDTO;

public interface WarehouseService {
    List<StoreDTO> getAllStores();
    StoreDTO addStore(StoreDTO dto);
    boolean toggleStoreStatus(Long id);
    void deleteStoreById(Long id);
}

