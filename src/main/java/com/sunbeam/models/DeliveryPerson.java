package com.sunbeam.models;

import java.time.Instant;

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
@Table(name = "delivery_persons")
@Data
public class DeliveryPerson {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String phone;

	@ManyToOne
	@JoinColumn(name = "warehouse_id", foreignKey = @ForeignKey(name = "delivery_persons_warehouse_id_warehouses_id_fk"))
	private Warehouse warehouse;

	@ManyToOne
	@JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "delivery_persons_order_id_orders_id_fk"))
	private Order order;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant updatedAt;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
}
