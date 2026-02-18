package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.ProductDto;
import com.SpringBootRESTAPIs.store.entities.Product;
import com.SpringBootRESTAPIs.store.mappers.ProductMapper;
import com.SpringBootRESTAPIs.store.repositories.CategoryRepository;
import com.SpringBootRESTAPIs.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

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
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id){
        var product = productRepository.findById(id).orElse(null);
        if(product == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder
    )
    {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null); // we need to fetch the category from the database to set it in the product entity before saving it,
        if(category == null)                                                                       // because the product entity has a many-to-one relationship with the category entity, so we need to set the category in the product entity before saving it (because category_id must be set in the database for a product).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        var savedProductDto = productMapper.toDto(productRepository.save(product));
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(savedProductDto.getId()).toUri();
        return ResponseEntity.created(uri).body(savedProductDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto
    )
    {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null); // we need to fetch the category from the database to set it in the product entity before saving it,
        if(category == null)                                                                       // because the product entity has a many-to-one relationship with the category entity, so we need to set the category in the product entity before saving it (because category_id must be set in the database for a product).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        product.setCategory(category);
        productMapper.update(productDto, product); // in this update implementation of mapstruct, you can find under target/annotations/ProductMapperImpl.java the category is not updated. that's why we need to set the category before calling the update method, because the update method will not update the category field in the product entity, so we need to set it before calling the update method, and then we will update the other fields of the product entity with the values from the productDto.
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(productMapper.toDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct (
            @PathVariable Long id
    )
    {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        productRepository.delete(product);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
