package com.robertson.chores.services;

import com.robertson.chores.dto.CategoryDTO;
import com.robertson.chores.dto.ChoreDTO;
import com.robertson.chores.exceptions.DuplicateResourceException;
import com.robertson.chores.exceptions.ResourceNotFoundException;
import com.robertson.chores.repositories.CategoryRepository;
import com.robertson.chores.models.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO);
    }

    public CategoryDTO save(CategoryDTO categoryDTO) {
        // Check for duplicates on create
        if (categoryDTO.getId() == null && categoryRepository.existsByName(categoryDTO.getName())) {
            throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        // Check for duplicates on update
        if (categoryDTO.getId() != null &&
                categoryRepository.existsByNameAndIdNot(categoryDTO.getName(), categoryDTO.getId())) {
            throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return toDTO(savedCategory);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    public void deleteById(Long id, boolean force) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!force && category.getChores() != null && !category.getChores().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing chores. Use force=true to delete category and all its chores.");
        }

        categoryRepository.deleteById(id);
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getChores() != null) {
            dto.setChores(category.getChores().stream()
                    .map(chore -> {
                        ChoreDTO choreDTO = new ChoreDTO();
                        choreDTO.setId(chore.getId());
                        choreDTO.setName(chore.getName());
                        choreDTO.setCategoryId(category.getId());
                        return choreDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());

        return category;
    }
}