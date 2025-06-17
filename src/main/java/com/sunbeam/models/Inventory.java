package com.sunbeam.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "inventories")
@Data
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String sku;

	@ManyToOne
	@JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "inventories_order_id_orders_id_fk"))
	private Order order;

	@ManyToOne
	@JoinColumn(name = "warehouse_id", foreignKey = @ForeignKey(name = "inventories_warehouse_id_warehouses_id_fk"))
	private Warehouse warehouse;

	@ManyToOne
	@JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "inventories_product_id_products_id_fk"))
	private Product product;

	@CreationTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	@UpdateTimestamp
	@Column(name = "created_at")
	private Instant createdAt;
}
