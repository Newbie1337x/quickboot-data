package com.newbie.app.example.repository;

import org.springframework.stereotype.Repository;

import com.newbie.app.common.base.repository.BaseRepository;
import com.newbie.app.example.entity.Example;

@Repository
public interface ExampleRepository extends BaseRepository<Example, Long> {
}
