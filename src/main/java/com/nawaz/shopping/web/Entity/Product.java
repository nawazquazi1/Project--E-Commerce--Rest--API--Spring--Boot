package com.nawaz.shopping.web.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;

	@NotBlank
	@Size(min = 3, message = "Product name must contain atleast 3 characters")
	private String productName;
	
	private String image;
	
	@NotBlank
	@Size(min = 6, message = "Product description must contain atleast 6 characters")
	private String description;
	
	private Integer quantity;
//	@NotBlank(message = "price should be greater thane 0")
	@Column(nullable = false)
	private double price;
	private double discount;
	private double specialPrice;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "product", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	private List<CartItem> products = new ArrayList<>();
	
	@OneToMany(mappedBy = "product", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<OrderItem> orderItems = new ArrayList<>();

}
