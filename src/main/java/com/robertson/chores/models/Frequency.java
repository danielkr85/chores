package com.robertson.chores.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Frequency {

    @Enumerated(EnumType.STRING)
    private FrequencyType type;

    @Enumerated(EnumType.STRING)
    private ChoreDay choreDay;

    // Validation: dayOfWeek should only be set if type is WEEKLY
    public void setType(FrequencyType type) {
        this.type = type;
        if (type == FrequencyType.DAILY) {
            this.choreDay = null;
        }
    }
}