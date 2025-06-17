package com.sunbeam.product;

public class ProductNotFound extends RuntimeException {

	public ProductNotFound(String msg) {
		super(msg);
	}
}
