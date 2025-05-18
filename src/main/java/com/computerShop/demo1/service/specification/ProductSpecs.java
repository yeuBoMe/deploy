package com.computerShop.demo1.service.specification;

import com.computerShop.demo1.domain.Product;
import com.computerShop.demo1.domain.Product_;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecs {

public static Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Product_.NAME)),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<Product> factoryLike(String factory) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Product_.FACTORY)),
                        "%" + factory.toLowerCase() + "%"
                );
    }

    public static Specification<Product> listFactoryLike(List<String> factories) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(Product_.FACTORY)).value(factories);
    }

    public static Specification<Product> hasExactFactory(String factory) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Product_.FACTORY), factory);
    }

/*    public static Specification<Product> listFactoryLike(List<String> factories) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> factoriesExpression = criteriaBuilder.lower(root.get(Product_.FACTORY));
            List<String> lowerFactories = factories.stream()
                    .map(String::toLowerCase)
                    .toList();
            return factoriesExpression.in(lowerFactories);
        };
    }*/

    public static Specification<Product> targetLike(String target) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Product_.TARGET)),
                        "%" + target.toLowerCase() + "%"
                );
    }

    public static Specification<Product> listTargetLike(List<String> targets) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(Product_.TARGET)).value(targets);
    }

    public static Specification<Product> priceGreaterThanOrEqual(double price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get(Product_.PRICE), price);
    }

    public static Specification<Product> priceLessThanOrEqual(double price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.le(root.get(Product_.PRICE), price);
    }

/*    public static Specification<Product> priceRange(double min, double max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.ge(root.get(Product_.PRICE), min),
                        criteriaBuilder.le(root.get(Product_.PRICE), max)
                );
    }*/

    public static Specification<Product> priceRange(double min, double max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(Product_.PRICE), min, max);
    }
}
