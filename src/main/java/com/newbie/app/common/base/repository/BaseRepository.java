package com.newbie.app.common.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Generic Base Repository providing standard JPA operations.
 * Annotated with @NoRepositoryBean to prevent Spring from creating an instance of this interface.
 *
 * @param <T>  The Entity type
 * @param <ID> The type of the Entity's identifier
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
}