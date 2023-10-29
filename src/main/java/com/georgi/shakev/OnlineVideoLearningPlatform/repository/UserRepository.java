package com.georgi.shakev.OnlineVideoLearningPlatform.repository;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByUsername(String username);
    Page<User> getAllByUsernameContainingIgnoreCase(String keyword, Pageable pageable);
}
