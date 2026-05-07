package com.newbie.app.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbie.app.common.base.controller.BaseController;
import com.newbie.app.example.dto.ExampleRequest;
import com.newbie.app.example.dto.ExampleResponse;
import com.newbie.app.example.entity.Example;
import com.newbie.app.example.mapper.ExampleMapper;
import com.newbie.app.example.service.ExampleService;

@RestController
@RequestMapping("/api/examples")
@Tag(name = "Example Module", description = "Endpoints for managing Example items")
public class ExampleController extends BaseController<Example, ExampleRequest, ExampleResponse, Long> {

    public ExampleController(ExampleService service, ExampleMapper mapper) {
        super(service, mapper);
    }
}
