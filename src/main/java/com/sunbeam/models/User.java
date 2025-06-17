package com.sunbeam.models;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name; // Full name of the user

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private String provider; // OAuth provider (e.g., Google, Facebook)

	private String externalId; // External ID from OAuth provider

	private String image; // Profile image URL of the user

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role; // Role of the user (ADMIN, CUSTOMER, DELIVERY_PERSON)

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;

	@Builder.Default
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
	private boolean enabled = true; // Indicates if the user account is active or disabled
	// we can set enable false when user deletes there account

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Order> orders; // List of orders associated with the user
}
