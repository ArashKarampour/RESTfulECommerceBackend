package com.SpringBootRESTAPIs.store.repositories;

import com.SpringBootRESTAPIs.store.dtos.ProductDto;
import com.SpringBootRESTAPIs.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = {"category"}) // this will fetch the category along with the product.
    List<Product> findByCategoryId(Byte categoryId);

    @EntityGraph(attributePaths = {"category"}) // this will fetch the category along with the product.
    @Query("SELECT p FROM Product p")
    List<Product> findAllWithCategory();
}