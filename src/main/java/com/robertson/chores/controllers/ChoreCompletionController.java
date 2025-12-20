package com.robertson.chores.controllers;

import com.robertson.chores.dto.ChoreCompletionDTO;
import com.robertson.chores.services.ChoreCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chores")
@RequiredArgsConstructor
public class ChoreCompletionController {
    private final ChoreCompletionService choreCompletionService;

    /**
     * Mark a chore as complete with current timestamp
     */
    @PostMapping("/{choreId}/complete")
    public ResponseEntity<ChoreCompletionDTO> markChoreComplete(
            @PathVariable Long choreId,
            @RequestParam(required = false) String notes) {
        ChoreCompletionDTO completion = choreCompletionService.markChoreComplete(choreId, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(completion);
    }

    /**
     * Mark a chore as complete with a specific timestamp
     */
    @PostMapping("/{choreId}/complete-at")
    public ResponseEntity<ChoreCompletionDTO> markChoreCompleteAt(
            @PathVariable Long choreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime completedAt,
            @RequestParam(required = false) String notes) {
        ChoreCompletionDTO completion = choreCompletionService.markChoreCompleteAt(choreId, completedAt, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(completion);
    }

    /**
     * Get all completions for a specific chore
     */
    @GetMapping("/{choreId}/completions")
    public ResponseEntity<List<ChoreCompletionDTO>> getChoreCompletions(@PathVariable Long choreId) {
        List<ChoreCompletionDTO> completions = choreCompletionService.getChoreCompletions(choreId);
        return ResponseEntity.ok(completions);
    }

    /**
     * Get completions for a specific chore within a date range
     */
    @GetMapping("/{choreId}/completions/range")
    public ResponseEntity<List<ChoreCompletionDTO>> getChoreCompletionsByDateRange(
            @PathVariable Long choreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ChoreCompletionDTO> completions = choreCompletionService.getChoreCompletionsByDateRange(choreId, startDate, endDate);
        return ResponseEntity.ok(completions);
    }

    /**
     * Get all completions
     */
    @GetMapping("/completions")
    public ResponseEntity<List<ChoreCompletionDTO>> getAllCompletions() {
        List<ChoreCompletionDTO> completions = choreCompletionService.getAllCompletions();
        return ResponseEntity.ok(completions);
    }

    /**
     * Get the most recent completion for a chore
     */
    @GetMapping("/{choreId}/completions/latest")
    public ResponseEntity<ChoreCompletionDTO> getLatestCompletion(@PathVariable Long choreId) {
        return choreCompletionService.getLatestCompletion(choreId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a specific completion record
     */
    @GetMapping("/completions/{completionId}")
    public ResponseEntity<ChoreCompletionDTO> getCompletionById(@PathVariable Long completionId) {
        return choreCompletionService.getCompletionById(completionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a completion record
     */
    @PutMapping("/completions/{completionId}")
    public ResponseEntity<ChoreCompletionDTO> updateCompletion(
            @PathVariable Long completionId,
            @RequestBody ChoreCompletionDTO dto) {
        ChoreCompletionDTO updatedCompletion = choreCompletionService.updateCompletion(completionId, dto);
        return ResponseEntity.ok(updatedCompletion);
    }

    /**
     * Delete a completion record
     */
    @DeleteMapping("/completions/{completionId}")
    public ResponseEntity<Void> deleteCompletion(@PathVariable Long completionId) {
        choreCompletionService.deleteCompletion(completionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all completions for a category
     */
    @GetMapping("/category/{categoryId}/completions")
    public ResponseEntity<List<ChoreCompletionDTO>> getCategoryCompletions(@PathVariable Long categoryId) {
        List<ChoreCompletionDTO> completions = choreCompletionService.getCategoryCompletions(categoryId);
        return ResponseEntity.ok(completions);
    }

    /**
     * Get completions for a category within a date range
     */
    @GetMapping("/category/{categoryId}/completions/range")
    public ResponseEntity<List<ChoreCompletionDTO>> getCategoryCompletionsByDateRange(
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ChoreCompletionDTO> completions = choreCompletionService.getCategoryCompletionsByDateRange(categoryId, startDate, endDate);
        return ResponseEntity.ok(completions);
    }
}

