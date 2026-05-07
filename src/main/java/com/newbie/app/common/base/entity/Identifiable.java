package com.newbie.app.common.base.entity;

/**
 * Contract for entities that expose a settable identifier.
 * Implementing this interface allows the generic {@code BaseService}
 * to set the ID during full-update operations without resorting
 * to fragile reflection-based lookups.
 *
 * @param <ID> The type of the entity's identifier
 */
public interface Identifiable<ID> {

    /**
     * Sets the entity's identifier.
     *
     * @param id The identifier to assign
     */
    void setId(ID id);
}
