package org.finance.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia generica per la persistenza dei dati.
 * Implementa il pattern Repository per astrarre la logica di accesso ai dati.
 */
public interface Repository<T, ID> {
    
    /**
     * Salva un'entità nel repository.
     */
    T save(T entity);
    
    /**
     * Trova un'entità per ID.
     */
    Optional<T> findById(ID id);
    
    /**
     * Trova tutte le entità.
     */
    List<T> findAll();
    
    /**
     * Elimina un'entità per ID.
     */
    void deleteById(ID id);
    
    /**
     * Verifica se esiste un'entità con l'ID specificato.
     */
    boolean existsById(ID id);
    
    /**
     * Conta il numero totale di entità.
     */
    long count();
}
