package com.example.springbootwithangular.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.repository.ProductRepository;

@Service
public class ProductService {
	private final ProductRepository repo;

	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	public List<Product> findAll() {
		return repo.findAll();
	}

	public Product save(Product product) {
		if (product.getCategory() == null || product.getCategory().isBlank()) {
			product.setCategory("Other");
		}
		return repo.save(product);
	}

	public Product findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	public void deleteById(Long id) {
		repo.deleteById(id);
	}
}
