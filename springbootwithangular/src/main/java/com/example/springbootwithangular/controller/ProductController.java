package com.example.springbootwithangular.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.service.ProductRecommendationService;
import com.example.springbootwithangular.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
	private final ProductService service;
	private final ProductRecommendationService recommendationService;

	public ProductController(ProductService service, ProductRecommendationService recommendationService) {
		this.service = service;
		this.recommendationService = recommendationService;
	}

	@GetMapping
	public List<Product> list() {
		return service.findAll();
	}

	@GetMapping("/recommendations")
	public List<Product> recommendations(@RequestParam(defaultValue = "") String query) {
		return recommendationService.recommend(service.findAll(), query);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> get(@PathVariable Long id) {
		Product product = service.findById(id);
		return product == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(product);
	}

	@PostMapping
	public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
		Product saved = service.save(product);
		return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
		Product existing = service.findById(id);
		if (existing == null) {
			return ResponseEntity.notFound().build();
		}
		product.setId(id);
		return ResponseEntity.ok(service.save(product));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
