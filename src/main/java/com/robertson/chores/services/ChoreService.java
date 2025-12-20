package com.robertson.chores.services;

import com.robertson.chores.dto.ChoreDTO;
import com.robertson.chores.dto.FrequencyDTO;
import com.robertson.chores.exceptions.DuplicateResourceException;
import com.robertson.chores.exceptions.ResourceNotFoundException;
import com.robertson.chores.models.Category;
import com.robertson.chores.models.Chore;
import com.robertson.chores.models.Frequency;
import com.robertson.chores.models.FrequencyType;
import com.robertson.chores.repositories.CategoryRepository;
import com.robertson.chores.repositories.ChoreRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChoreService {

    private final ChoreRepository choreRepository;
    private final CategoryRepository categoryRepository;

    public List<ChoreDTO> findAll() {
        return choreRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ChoreDTO> findById(Long id) {
        return choreRepository.findById(id)
                .map(this::toDTO);
    }

    public ChoreDTO save(ChoreDTO choreDTO) {
        // Validate frequency if provided
        if (choreDTO.getFrequency() != null) {
            validateFrequency(choreDTO.getFrequency());
        }

        // Check for duplicates on create
        if (choreDTO.getId() == null &&
                choreRepository.existsByNameAndCategory_Id(choreDTO.getName(), choreDTO.getCategoryId())) {
            throw new DuplicateResourceException("Chore with name '" + choreDTO.getName() +
                    "' already exists in this category");
        }

        // Check for duplicates on update
        if (choreDTO.getId() != null &&
                choreRepository.existsByNameAndCategory_IdAndIdNot(
                        choreDTO.getName(), choreDTO.getCategoryId(), choreDTO.getId())) {
            throw new DuplicateResourceException("Chore with name '" + choreDTO.getName() +
                    "' already exists in this category");
        }

        Chore chore = toEntity(choreDTO);

        try {
            Chore savedChore = choreRepository.save(chore);
            return toDTO(savedChore);
        } catch (Exception e) {
            // Catch any persistence exceptions and provide better error messages
            if (e.getMessage() != null && e.getMessage().contains("not-null property")) {
                throw new RuntimeException("Invalid chore data: " + e.getMessage());
            }
            throw e;
        }
    }

    public void deleteById(Long id) {
        choreRepository.deleteById(id);
    }

    // Mapping methods
    private ChoreDTO toDTO(Chore chore) {
        ChoreDTO dto = new ChoreDTO();
        dto.setId(chore.getId());
        dto.setName(chore.getName());
        dto.setCategoryId(chore.getCategory() != null ? chore.getCategory().getId() : null);

        if (chore.getFrequency() != null && chore.getFrequency().getType() != null) {
            FrequencyDTO frequencyDTO = new FrequencyDTO(
                    chore.getFrequency().getType(),
                    chore.getFrequency().getChoreDay()
            );
            dto.setFrequency(frequencyDTO);
        }

        return dto;
    }

    private Chore toEntity(ChoreDTO dto) {
        Chore chore = new Chore();
        chore.setId(dto.getId());
        chore.setName(dto.getName());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            chore.setCategory(category);
        }

        // Only set frequency if it's provided in the DTO
        if (dto.getFrequency() != null) {
            Frequency frequency = new Frequency(
                    dto.getFrequency().getType(),
                    dto.getFrequency().getChoreDay()
            );
            chore.setFrequency(frequency);
        }

        return chore;
    }

    private void validateFrequency(FrequencyDTO frequency) {
        if (frequency.getType() == null) {
            throw new RuntimeException("Frequency type is required when frequency is provided");
        }

        if (frequency.getType() == FrequencyType.WEEKLY && frequency.getChoreDay() == null) {
            throw new RuntimeException("Chore day is required for weekly frequency");
        }

        if (frequency.getType() == FrequencyType.DAILY && frequency.getChoreDay() != null) {
            throw new RuntimeException("Chore day should not be specified for daily frequency");
        }
    }
}