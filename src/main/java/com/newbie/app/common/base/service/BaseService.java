package com.newbie.app.common.base.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.newbie.app.common.base.entity.Identifiable;
import com.newbie.app.common.base.repository.BaseRepository;
import com.newbie.app.common.exception.ResourceNotFoundException;
import com.newbie.app.common.util.BeanUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic Base Service providing transactional CRUD business logic with lifecycle hooks.
 *
 * <p>The {@code T extends Identifiable<ID>} bound guarantees that all entity types
 * expose a {@code setId} method at compile time, avoiding fragile reflection-based lookups
 * during full-update operations.</p>
 *
 * <p>All state-mutating methods are annotated with {@code @Transactional} to ensure
 * atomicity and automatic rollback on unchecked exceptions. Read operations use
 * {@code @Transactional(readOnly = true)} for performance optimization.</p>
 *
 * @param <T>  The Entity type (must implement {@link Identifiable})
 * @param <ID> The type of the Entity's identifier
 */
public abstract class BaseService<T extends Identifiable<ID>, ID extends Serializable> {

    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all records with pagination support.
     *
     * @param pageable Pagination and sorting information
     * @return A page of entities
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(@NonNull Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Retrieves all records without pagination.
     *
     * @return List of all entities
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    /**
     * Finds a record by its unique identifier.
     *
     * @param id The identifier
     * @return An Optional containing the entity if found
     */
    @Transactional(readOnly = true)
    public Optional<T> findById(@NonNull ID id) {
        return repository.findById(id);
    }

    /**
     * Persists a new entity. Triggers beforeSave and afterSave hooks.
     *
     * @param entity The entity to save
     * @return The persisted entity
     */
    @Transactional
    public T save(@NonNull T entity) {
        beforeSave(entity);
        T saved = repository.save(entity);
        afterSave(saved);
        return saved;
    }

    /**
     * Deletes a record by its identifier. Triggers beforeDelete and afterDelete hooks.
     *
     * @param id The identifier of the record to delete
     */
    @Transactional
    public void deleteById(@NonNull ID id) {
        beforeDelete(id);
        repository.deleteById(id);
        afterDelete(id);
    }

    /**
     * Performs a full update of an existing record.
     * Uses the {@link Identifiable} contract to set the ID directly — no reflection needed.
     * Triggers beforeUpdate and afterUpdate hooks.
     *
     * @param id     The identifier of the record to update
     * @param entity The new data
     * @return The updated entity
     */
    @Transactional
    public T update(@NonNull ID id, @NonNull T entity) {
        entity.setId(id);
        beforeUpdate(entity);
        T updated = repository.save(entity);
        afterUpdate(updated);
        return updated;
    }

    /**
     * Performs a partial update (PATCH) of an existing record.
     * Only non-null fields from the input entity will be copied to the existing record.
     * The fields {@code id}, {@code createdAt}, and {@code updatedAt} are always ignored.
     *
     * @param id     The identifier of the record to update
     * @param entity The partial data
     * @return The updated entity
     */
    @Transactional
    public T patch(@NonNull ID id, @NonNull T entity) {
        T existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

        BeanUtil.copyNonNullProperties(entity, existing);

        beforeUpdate(existing);
        T updated = repository.save(existing);
        afterUpdate(updated);
        return updated;
    }

    // ==========================================
    // LIFECYCLE HOOKS (Override as needed)
    // ==========================================

    protected void beforeSave(@NonNull T entity) {
    }

    protected void afterSave(@NonNull T entity) {
    }

    protected void beforeUpdate(@NonNull T entity) {
    }

    protected void afterUpdate(@NonNull T entity) {
    }

    protected void beforeDelete(ID id) {
    }

    protected void afterDelete(ID id) {
    }
}
