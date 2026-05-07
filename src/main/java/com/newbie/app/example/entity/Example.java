package com.newbie.app.example.entity;

import com.newbie.app.common.base.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Example entity to demonstrate the generic CRUD architecture.
 * This can be safely deleted once you start your real business logic.
 */
@Entity
@Table(name = "examples")
@Getter
@Setter
public class Example extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Column
    private String description;
}
