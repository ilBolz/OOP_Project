package org.finance.repository;
import java.util.List;
import java.util.Optional;
/**
 * Generic interface for data persistence.
 * Implements the Repository pattern to abstract data access logic.
 */
public interface Repository<T, ID> {
    /**
     * Saves an entity in the repository.
     */
    T save(T entity);
    /**
     * Finds an entity by ID.
     */
    Optional<T> findById(ID id);
    /**
     * Finds all entities.
     */
    List<T> findAll();
    /**
     * Deletes an entity by ID.
     */
    void deleteById(ID id);
    /**
     * Checks if an entity exists with the specified ID.
     */
    boolean existsById(ID id);
    /**
     * Counts the total number of entities.
     */
    long count();
}



