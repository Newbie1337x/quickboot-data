package com.newbie.app.example.service;

import org.springframework.stereotype.Service;

import com.newbie.app.common.base.service.BaseService;
import com.newbie.app.example.entity.Example;
import com.newbie.app.example.repository.ExampleRepository;

@Service
public class ExampleService extends BaseService<Example, Long> {
    public ExampleService(ExampleRepository repository) {
        super(repository);
    }
}
