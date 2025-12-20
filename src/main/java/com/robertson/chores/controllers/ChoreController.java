package com.robertson.chores.controllers;

import com.robertson.chores.dto.ChoreDTO;
import com.robertson.chores.services.ChoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chores")
@RequiredArgsConstructor
public class ChoreController {
    private final ChoreService choreService;

    @GetMapping
    public List<ChoreDTO> getAllChores() {
        return choreService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChoreDTO> getChoreById(@PathVariable Long id) {
        return choreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ChoreDTO createChore(@RequestBody ChoreDTO choreDTO) {
        return choreService.save(choreDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChoreDTO> updateChore(@PathVariable Long id, @RequestBody ChoreDTO choreDTO) {
        return choreService.findById(id)
                .map(existingChore -> {
                    existingChore.setName(choreDTO.getName());
                    existingChore.setCategoryId(choreDTO.getCategoryId());
                    existingChore.setFrequency(choreDTO.getFrequency());
                    return ResponseEntity.ok(choreService.save(existingChore));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChore(@PathVariable Long id) {
        return choreService.findById(id)
                .map(existingChore -> {
                    choreService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}