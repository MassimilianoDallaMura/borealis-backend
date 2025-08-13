package com.borealis.backend.service;

import com.borealis.backend.dto.request.CategoryRequestDTO;
import com.borealis.backend.dto.response.CategoryResponseDTO;
import com.borealis.backend.model.Category;
import com.borealis.backend.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper; // Importa ModelMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper; // Inietta ModelMapper

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Crea una nuova categoria.
     * @param categoryDto DTO della categoria da creare.
     * @return CategoryResponseDTO della categoria creata.
     * @throws IllegalArgumentException se il nome della categoria è già in uso.
     */
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Errore: Il nome della categoria '" + categoryDto.getName() + "' è già in uso.");
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    /**
     * Recupera una categoria per ID.
     * @param id ID della categoria.
     * @return CategoryResponseDTO se trovata.
     * @throws IllegalArgumentException se la categoria non è trovata.
     */
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria con ID " + id + " non trovata."));
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    /**
     * Recupera tutte le categorie.
     * @return Lista di CategoryResponseDTO.
     */
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Aggiorna una categoria esistente.
     * @param id ID della categoria da aggiornare.
     * @param categoryDto DTO con i dati aggiornati.
     * @return CategoryResponseDTO della categoria aggiornata.
     * @throws IllegalArgumentException se la categoria non è trovata o il nuovo nome è già in uso.
     */
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria con ID " + id + " non trovata per l'aggiornamento."));

        // Verifica se il nuovo nome è già in uso da un'altra categoria (escludendo se stessa)
        if (categoryRepository.findByName(categoryDto.getName())
                .filter(cat -> !cat.getId().equals(id))
                .isPresent()) {
            throw new IllegalArgumentException("Errore: Il nome della categoria '" + categoryDto.getName() + "' è già in uso da un'altra categoria.");
        }

        existingCategory.setName(categoryDto.getName()); // Aggiorna il nome
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryResponseDTO.class);
    }

    /**
     * Elimina una categoria.
     * @param id ID della categoria da eliminare.
     * @throws IllegalArgumentException se la categoria non è trovata.
     */
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoria con ID " + id + " non trovata per l'eliminazione.");
        }
        // TODO: Considera la gestione dei prodotti associati a questa categoria prima dell'eliminazione.
        // Potrebbe essere necessario disassociare i prodotti o eliminarli a cascata.
        categoryRepository.deleteById(id);
    }
}
