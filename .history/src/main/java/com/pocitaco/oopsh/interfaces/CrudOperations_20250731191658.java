package com.pocitaco.oopsh.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD operations interface
 * Provides basic Create, Read, Update, Delete operations
 * 
 * @param <T> The entity type
 * @param <ID> The ID type
 */
public interface CrudOperations<T, ID> {
    
    /**
     * Create a new entity
     * 
     * @param entity The entity to create
     * @return The created entity with generated ID
     */
    T create(T entity);
    
    /**
     * Find entity by ID
     * 
     * @param id The entity ID
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * 
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Update an existing entity
     * 
     * @param entity The entity to update
     * @return The updated entity
     */
    T update(T entity);
    
    /**
     * Delete entity by ID
     * 
     * @param id The entity ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteById(ID id);
    
    /**
     * Check if entity exists by ID
     * 
     * @param id The entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Count total number of entities
     * 
     * @return Total count
     */
    long count();
    
    /**
     * Save entity (create if new, update if exists)
     * 
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);
}
