package com.robertson.chores.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChoreCompletionDTO {
    private Long id;
    private Long choreId;
    private LocalDateTime completedAt;
    private String notes;
}

