package com.jseric.simple_product_rest.repository;

import com.jseric.simple_product_rest.model.product.Product;
import com.jseric.simple_product_rest.repository.base.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends SoftDeleteRepository<Product, Integer> {

    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.code = ?1 AND p.deleted IS NULL")
    long countByCode(final String code);
}
