package com.robertson.chores.services;

import com.robertson.chores.dto.ChoreCompletionDTO;
import com.robertson.chores.exceptions.ResourceNotFoundException;
import com.robertson.chores.models.Chore;
import com.robertson.chores.models.ChoreCompletion;
import com.robertson.chores.repositories.ChoreCompletionRepository;
import com.robertson.chores.repositories.ChoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChoreCompletionService {

    private final ChoreCompletionRepository choreCompletionRepository;
    private final ChoreRepository choreRepository;

    /**
     * Records a new chore completion with the current timestamp
     */
    public ChoreCompletionDTO markChoreComplete(Long choreId, String notes) {
        Chore chore = choreRepository.findById(choreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chore not found with id: " + choreId));

        ChoreCompletion completion = new ChoreCompletion();
        completion.setChore(chore);
        completion.setCompletedAt(LocalDateTime.now());
        completion.setNotes(notes);

        ChoreCompletion savedCompletion = choreCompletionRepository.save(completion);
        return toDTO(savedCompletion);
    }

    /**
     * Records a new chore completion with a specific timestamp
     */
    public ChoreCompletionDTO markChoreCompleteAt(Long choreId, LocalDateTime completedAt, String notes) {
        Chore chore = choreRepository.findById(choreId)
                .orElseThrow(() -> new ResourceNotFoundException("Chore not found with id: " + choreId));

        ChoreCompletion completion = new ChoreCompletion();
        completion.setChore(chore);
        completion.setCompletedAt(completedAt);
        completion.setNotes(notes);

        ChoreCompletion savedCompletion = choreCompletionRepository.save(completion);
        return toDTO(savedCompletion);
    }

    /**
     * Get all completions for a specific chore
     */
    public List<ChoreCompletionDTO> getChoreCompletions(Long choreId) {
        // Verify chore exists
        if (!choreRepository.existsById(choreId)) {
            throw new ResourceNotFoundException("Chore not found with id: " + choreId);
        }

        return choreCompletionRepository.findByChore_IdOrderByCompletedAtDesc(choreId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get completions for a specific chore within a date range
     */
    public List<ChoreCompletionDTO> getChoreCompletionsByDateRange(Long choreId, LocalDateTime startDate, LocalDateTime endDate) {
        // Verify chore exists
        if (!choreRepository.existsById(choreId)) {
            throw new ResourceNotFoundException("Chore not found with id: " + choreId);
        }

        return choreCompletionRepository.findByChore_IdAndCompletedAtBetweenOrderByCompletedAtDesc(choreId, startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all completions for a category
     */
    public List<ChoreCompletionDTO> getCategoryCompletions(Long categoryId) {
        return choreCompletionRepository.findByCategoryIdOrderByCompletedAtDesc(categoryId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get completions for a category within a date range
     */
    public List<ChoreCompletionDTO> getCategoryCompletionsByDateRange(Long categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        return choreCompletionRepository.findByCategoryIdAndDateRange(categoryId, startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all completions regardless of chore or category
     */
    public List<ChoreCompletionDTO> getAllCompletions() {
        return choreCompletionRepository.findAllByOrderByCompletedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific completion record
     */
    public Optional<ChoreCompletionDTO> getCompletionById(Long completionId) {
        return choreCompletionRepository.findById(completionId)
                .map(this::toDTO);
    }

    /**
     * Update a completion record
     */
    public ChoreCompletionDTO updateCompletion(Long completionId, ChoreCompletionDTO dto) {
        ChoreCompletion completion = choreCompletionRepository.findById(completionId)
                .orElseThrow(() -> new ResourceNotFoundException("Completion not found with id: " + completionId));

        if (dto.getCompletedAt() != null) {
            completion.setCompletedAt(dto.getCompletedAt());
        }
        if (dto.getNotes() != null) {
            completion.setNotes(dto.getNotes());
        }

        ChoreCompletion updatedCompletion = choreCompletionRepository.save(completion);
        return toDTO(updatedCompletion);
    }

    /**
     * Delete a completion record
     */
    public void deleteCompletion(Long completionId) {
        if (!choreCompletionRepository.existsById(completionId)) {
            throw new ResourceNotFoundException("Completion not found with id: " + completionId);
        }
        choreCompletionRepository.deleteById(completionId);
    }

    /**
     * Get the most recent completion for a chore
     */
    public Optional<ChoreCompletionDTO> getLatestCompletion(Long choreId) {
        List<ChoreCompletionDTO> completions = getChoreCompletions(choreId);
        return completions.stream().findFirst();
    }

    // Mapping method
    private ChoreCompletionDTO toDTO(ChoreCompletion completion) {
        ChoreCompletionDTO dto = new ChoreCompletionDTO();
        dto.setId(completion.getId());
        dto.setChoreId(completion.getChore().getId());
        dto.setCompletedAt(completion.getCompletedAt());
        dto.setNotes(completion.getNotes());
        return dto;
    }
}

