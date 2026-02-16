package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.ProductDto;
import com.SpringBootRESTAPIs.store.entities.Product;
import com.SpringBootRESTAPIs.store.mappers.ProductMapper;
import com.SpringBootRESTAPIs.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public Iterable<ProductDto> getAllProducts(@RequestParam(required = false, defaultValue = "0", name="categoryId") Byte categoryId){
        // second approach(better performance): in this method we will filter the products by categoryId in the database query, so we will fetch only the products that belong to the categoryId, this is more efficient because we will fetch only the products that we need from the database, but we need to add a new method in the ProductRepository interface to filter by categoryId.
        List<Product> products;
        if (categoryId <= 0)
            products = productRepository.findAllWithCategory(); // instead of findAll, we will use findAllWithCategory to fetch the category along with the product, this is more efficient because we will fetch the category in the same query instead of fetching it in a separate query for each product.
        else
            products = productRepository.findByCategoryId(categoryId);

        return products.stream().map(productMapper::toDto).toList();

        // first approach: in this method we will filter the products by categoryId in java code, but this is not efficient because we will fetch all the products from the database and then filter them in java code, so if we have a lot of products, it will be very slow.
//        var products = productRepository.findAll()
//                .stream()
//                .map(productMapper::toDto)
//                .toList();
//        if(categoryId <= 0)
//            return products;
//        return products.stream().filter(productDto -> Objects.equals(productDto.getCategoryId(), categoryId)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") long id){
        var product = productRepository.findById(id).orElse(null);
        if(product == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productMapper.toDto(product));
    }
}
