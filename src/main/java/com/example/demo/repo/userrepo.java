package com.example.demo.repo;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface userrepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
