package com.robertson.chores.services;

import com.robertson.chores.dto.ChoreDTO;
import com.robertson.chores.exceptions.DuplicateResourceException;
import com.robertson.chores.exceptions.ResourceNotFoundException;
import com.robertson.chores.models.Category;
import com.robertson.chores.models.Chore;
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
        // Check for duplicates
        if (choreDTO.getId() == null &&
                choreRepository.existsByNameAndCategory_Id(choreDTO.getName(), choreDTO.getCategoryId())) {
            throw new DuplicateResourceException("Chore with name '" + choreDTO.getName() +
                    "' already exists in this category");
        }

        if (choreDTO.getId() != null &&
                choreRepository.existsByNameAndCategory_IdAndIdNot(
                        choreDTO.getName(), choreDTO.getCategoryId(), choreDTO.getId())) {
            throw new DuplicateResourceException("Chore with name '" + choreDTO.getName() +
                    "' already exists in this category");
        }

        Chore chore = toEntity(choreDTO);
        Chore savedChore = choreRepository.save(chore);
        return toDTO(savedChore);
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

        return chore;
    }
}
