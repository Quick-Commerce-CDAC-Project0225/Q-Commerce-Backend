package com.sunbeam.dto.inventory;

import java.time.Instant;
import com.sunbeam.models.Inventory;

public record InventoryDTO(
    Long id,
    String sku,
    Long productId,
    String productName,
    Long warehouseId,
    String warehouseName,
    Instant createdAt,
    Instant updatedAt
) {
    public static InventoryDTO from(Inventory inv) {
        return new InventoryDTO(
            inv.getId(),
            inv.getSku(),
            inv.getProduct() != null ? inv.getProduct().getId() : null,
            inv.getProduct() != null ? inv.getProduct().getName() : null,
            inv.getWarehouse() != null ? inv.getWarehouse().getId() : null,
            inv.getWarehouse() != null ? inv.getWarehouse().getName() : null,
            inv.getCreatedAt(),
            inv.getUpdatedAt()
        );
    }
}
