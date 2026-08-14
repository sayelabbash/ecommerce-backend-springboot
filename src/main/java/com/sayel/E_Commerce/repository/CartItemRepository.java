package com.sayel.E_Commerce.repository;

import com.sayel.E_Commerce.entity.CartItem;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
