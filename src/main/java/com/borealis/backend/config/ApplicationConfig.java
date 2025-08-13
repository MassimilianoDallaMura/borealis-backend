package com.borealis.backend.config;

import com.borealis.backend.dto.request.ProductRequestDTO; // Importa il DTO di richiesta
import com.borealis.backend.model.Product; // Importa l'entità Product

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies; // Importa MatchingStrategies
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configurazione per evitare l'ambiguità di setId()
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT); // Consigliato per mappature più precise

        // Mappatura esplicita per ProductRequestDTO a Product
        // Diciamo a ModelMapper di ignorare il tentativo di mappare qualsiasi cosa a Product.setId()
        // perché l'ID del prodotto è generato dal DB e non viene dal DTO di richiesta.
        // Questo risolve l'errore "matches multiple source property hierarchies"
        modelMapper.createTypeMap(ProductRequestDTO.class, Product.class)
                .addMappings(mapper -> {
                    mapper.skip(Product::setId); // Ignora il setter per l'ID del prodotto
                    // Se hai altri campi nel DTO che non devono essere mappati a Product, aggiungili qui.
                    // Esempio: se ProductRequestDTO avesse un campo 'sellerId' e Product avesse un 'setSeller(User seller)',
                    // ModelMapper potrebbe confondersi. In quel caso, dovresti mappare manualmente o skippare:
                    // mapper.map(src -> src.getSellerId(), Product::setSeller); // Se vuoi mappare l'ID al setter dell'oggetto
                    // O se non vuoi mappare affatto:
                    // mapper.skip(Product::setSeller);
                });

        // Puoi aggiungere qui altre configurazioni personalizzate, es. per cicli infiniti
        // o per mappare DTO complessi a entità e viceversa.

        return modelMapper;
    }
}