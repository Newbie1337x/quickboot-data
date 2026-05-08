package com.newbie.app.example.entity;
import com.newbie.app.common.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

}
