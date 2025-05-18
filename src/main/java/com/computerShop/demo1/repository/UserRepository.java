package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    Optional<User> findById(long id);

    void deleteById(long id);

    long count();

    User findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAll(Pageable pageable);
}
