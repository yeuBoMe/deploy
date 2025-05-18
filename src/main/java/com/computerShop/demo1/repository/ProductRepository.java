package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Product save(Product product);

    Optional<Product> findById(long id);

    void deleteById(long id);

    long count();

    boolean existsByNameAndFactory(String name, String factory);

    boolean existsByName(String name);

    // Count quantity depends on factory
    @Query("SELECT p.factory, COUNT(p) FROM Product p GROUP BY p.factory")
    List<Object[]> findFactoryCounts();

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
