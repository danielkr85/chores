package com.robertson.chores.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(name = "idx_chore_completion_timestamp", columnList = "chore_id,completed_at")})
public class ChoreCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chore_id", nullable = false)
    private Chore chore;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String notes;
}


