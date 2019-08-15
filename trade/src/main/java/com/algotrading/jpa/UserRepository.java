package com.algotrading.jpa;

import org.springframework.data.repository.CrudRepository;

import com.algotrading.user.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {

}