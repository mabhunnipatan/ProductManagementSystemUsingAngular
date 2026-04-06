package com.example.springbootwithangular.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbootwithangular.entity.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{

}
