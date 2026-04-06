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
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.service.ProductService;



@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // allow Angular dev server
public class ProductController {
	private final ProductService service;
	public ProductController(ProductService service){ this.service = service; }


	@GetMapping
	public List<Product> list(){ return service.findAll(); }


	@GetMapping("/{id}")
	public ResponseEntity<Product> get(@PathVariable Long id){
	Product p = service.findById(id);
	return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
	}


	@PostMapping
	public ResponseEntity<Product> create(@RequestBody Product product){
	Product saved = service.save(product);
	return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
	}


	@PutMapping("/{id}")
	public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product){
	Product existing = service.findById(id);
	if(existing == null) return ResponseEntity.notFound().build();
	product.setId(id);
	Product saved = service.save(product);
	return ResponseEntity.ok(saved);
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
	service.deleteById(id);
	return ResponseEntity.noContent().build();
	}
}
