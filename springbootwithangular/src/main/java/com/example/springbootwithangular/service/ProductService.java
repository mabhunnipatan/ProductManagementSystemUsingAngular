package com.example.springbootwithangular.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private  ProductRepository repo;
	


	public List<Product> findAll(){ return repo.findAll(); }
	public Product save(Product p){ return repo.save(p); }
	public Product findById(Long id){ return repo.findById(id).orElse(null); }
	public void deleteById(Long id){ repo.deleteById(id); }
}
