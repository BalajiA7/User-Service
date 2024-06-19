package org.example.user_service.repositories;

import org.example.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //Declared Query
    Optional<User> findUserByEmail(String email);
}
