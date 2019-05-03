package com.rohit.springbootrestoauth2security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rohit.springbootrestoauth2security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByusername(String username);
}
