package com.robertson.chores.repositories;

import com.robertson.chores.models.ChoreCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChoreCompletionRepository extends JpaRepository<ChoreCompletion, Long> {

    List<ChoreCompletion> findByChore_IdOrderByCompletedAtDesc(Long choreId);

    List<ChoreCompletion> findByChore_IdAndCompletedAtBetweenOrderByCompletedAtDesc(
            Long choreId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT cc FROM ChoreCompletion cc WHERE cc.chore.category.id = :categoryId ORDER BY cc.completedAt DESC")
    List<ChoreCompletion> findByCategoryIdOrderByCompletedAtDesc(@Param("categoryId") Long categoryId);

    @Query("SELECT cc FROM ChoreCompletion cc WHERE cc.chore.category.id = :categoryId " +
           "AND cc.completedAt BETWEEN :startDate AND :endDate ORDER BY cc.completedAt DESC")
    List<ChoreCompletion> findByCategoryIdAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

