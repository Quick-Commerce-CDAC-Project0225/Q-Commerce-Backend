package com.sunbeam.dto.order;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore null fields in JSON response
public class OrderRequestDto {
	private String address;

	@Builder.Default
	private String type = "quick"; // Default to "quick"
	private List<OrderProductDto> products;
}
