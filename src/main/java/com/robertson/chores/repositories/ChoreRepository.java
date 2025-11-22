package com.robertson.chores.repositories;

import com.robertson.chores.models.Chore;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoreRepository extends JpaRepository<Chore, Long> {
    boolean existsByNameAndCategory_Id(String name, Long categoryId);
    boolean existsByNameAndCategory_IdAndIdNot(String name, Long categoryId, Long id);
}
