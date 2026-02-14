package com.SpringBootRESTAPIs.store.repositories;

import com.SpringBootRESTAPIs.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}