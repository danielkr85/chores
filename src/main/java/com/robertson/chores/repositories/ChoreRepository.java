package com.robertson.chores.repositories;

import com.robertson.chores.models.Chore;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoreRepository extends JpaRepository<Chore, Long> {
}
