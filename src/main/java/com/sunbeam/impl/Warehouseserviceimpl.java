package com.sunbeam.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.sunbeam.dto.store.StoreDTO;

import com.sunbeam.models.Warehouse;

import com.sunbeam.repository.WarehouseRepository;

import com.sunbeam.service.WarehouseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Warehouseserviceimpl implements WarehouseService {

    private final WarehouseRepository repo;
    private final ModelMapper mapper;

    @Override
    public List<StoreDTO> getAllStores() {
        List<Warehouse> stores = repo.findAll();
        return stores.stream()
                .map(store -> mapper.map(store, StoreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StoreDTO addStore(StoreDTO dto) {
        Warehouse store = mapper.map(dto,  Warehouse.class);
        Warehouse saved = repo.save(store);
        return mapper.map(saved, StoreDTO.class);
    }

    @Override
    public boolean toggleStoreStatus(Long id) {
        Optional< Warehouse> optional = repo.findById(id);
        if (optional.isPresent()) {
        	 Warehouse store = optional.get();
            store.setStatus(store.getStatus() == 1 ? 0 : 1);
            repo.save(store);
            return true;
        }
        return false;
    }

    @Override
    public void deleteStoreById(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            log.info("Store with ID {} deleted successfully", id);
        } else {
            log.warn("Store with ID {} not found", id);
        }
    }
}
