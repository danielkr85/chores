package com.robertson.chores.dto;

import lombok.Data;

@Data
public class ChoreDTO {
    private Long id;
    private String name;
    private Long categoryId;
}
