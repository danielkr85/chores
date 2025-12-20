package com.robertson.chores.dto;

import com.robertson.chores.models.FrequencyType;
import com.robertson.chores.models.ChoreDay;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrequencyDTO {
    private FrequencyType type;
    private ChoreDay choreDay;
}