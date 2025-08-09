package com.sunbeam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sunbeam.dto.inventory.InventoryDTO;
import com.sunbeam.dto.inventory.InventoryUpsertRequest;

public interface InventoryService {
    Page<InventoryDTO> list(Pageable pageable);
    InventoryDTO get(Long id);
    InventoryDTO create(InventoryUpsertRequest req);
    InventoryDTO update(Long id, InventoryUpsertRequest req);
    void delete(Long id);
}

