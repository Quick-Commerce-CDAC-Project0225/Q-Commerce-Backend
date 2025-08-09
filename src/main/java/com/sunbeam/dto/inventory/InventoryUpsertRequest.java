// src/main/java/com/sunbeam/dto/inventory/InventoryUpsertRequest.java
package com.sunbeam.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryUpsertRequest(
        @NotBlank String sku,
        @NotNull Long productId,
        @NotNull Long warehouseId
) {}

