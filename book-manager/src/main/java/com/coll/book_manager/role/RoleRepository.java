package com.coll.book_manager.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    
    Optional<Role> findByName(String role);
}
