package com.borealis.backend.controller;

import com.borealis.backend.dto.request.CategoryRequestDTO;
import com.borealis.backend.dto.response.CategoryResponseDTO;
import com.borealis.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "https://bvm.kaninchenhaus.org", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories
     * Recupera tutte le categorie.
     * @return Lista di CategoryResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/categories/{id}
     * Recupera una categoria per ID.
     * @param id ID della categoria.
     * @return CategoryResponseDTO se trovata.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        try {
            CategoryResponseDTO category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /api/categories
     * Crea una nuova categoria.
     * @param categoryDto DTO della categoria da creare.
     * @return CategoryResponseDTO della categoria creata.
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryDto) {
        try {
            CategoryResponseDTO createdCategory = categoryService.createCategory(categoryDto);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /api/categories/{id}
     * Aggiorna una categoria esistente.
     * @param id ID della categoria da aggiornare.
     * @param categoryDto DTO con i dati aggiornati.
     * @return CategoryResponseDTO della categoria aggiornata.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryDto) {
        try {
            CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/categories/{id}
     * Elimina una categoria per ID.
     * @param id ID della categoria da eliminare.
     * @return ResponseEntity con stato NO_CONTENT in caso di successo.
     */
    @DeleteMapping("/{id}") // <-- NUOVO ENDPOINT PER L'ELIMINAZIONE
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content per eliminazione riuscita
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found se la categoria non esiste
        }
    }
}
