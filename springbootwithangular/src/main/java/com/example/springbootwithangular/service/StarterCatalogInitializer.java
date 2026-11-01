package com.example.springbootwithangular.service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.repository.ProductRepository;

@Component
public class StarterCatalogInitializer implements CommandLineRunner {
	private final ProductRepository repository;

	public StarterCatalogInitializer(ProductRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) {
		if (repository.count() > 0) {
			return;
		}

		repository.saveAll(List.of(
				product("City Runner Sneakers", "Lightweight everyday sneakers with cushioned support for long days on the move.", 2499, "Sneakers"),
				product("Weekend Canvas Tote", "A roomy, durable canvas carryall with an inside pocket for daily essentials.", 1299, "Bags"),
				product("Relaxed Cotton Shirt", "A breathable, easy-fit cotton shirt that works for casual days or layered looks.", 1799, "Clothing"),
				product("Everyday Crossbody Bag", "A compact crossbody with an adjustable strap and space for your daily essentials.", 1899, "Bags"),
				product("Soft Knit Pullover", "A soft, lightweight knit layer designed for comfortable all-day wear.", 2199, "Clothing"),
				product("Classic Metal Watch", "A clean, versatile timepiece with a minimal dial and an adjustable metal strap.", 3299, "Accessories"),
				product("Comfy Slide Sandals", "Easy slip-on sandals with a cushioned footbed for relaxed everyday comfort.", 999, "Footwear"),
				product("Ceramic Desk Planter", "A simple glazed ceramic planter to add a little green to a desk or shelf.", 799, "Home")));
	}

	private Product product(String name, String description, double price, String category) {
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setPrice(price);
		product.setCategory(category);
		return product;
	}
}
