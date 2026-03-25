package com.SpringBootRESTAPIs.store.repositories;

import com.SpringBootRESTAPIs.store.entities.Order;
import com.SpringBootRESTAPIs.store.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items.product") // to avoid n+1 problem, we will fetch the order items and their products in the same query as the orders.
    List<Order> findAllByCustomer(User customer);

    @EntityGraph(attributePaths = "items.product")
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> getOrderWithItems(@Param("id") Long id);
}