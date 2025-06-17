package com.sunbeam.dto.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for paginated API responses.
 * 
 * @param <T> Type of data being paginated.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDTO<T> {
	private boolean success;
	private String message;
	private List<T> data;
	private int page; // Current page number
	private int size; // Number of items per page
	private long totalElements; // Total number of records in the database
	private int totalPages; // Total number of pages available
	private boolean first; // Is this the first page?
	private boolean last; // Is this the last page?

}
