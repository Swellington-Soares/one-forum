package br.one.forum.controller;


import br.one.forum.dto.response.CategoryResponseDto;
import br.one.forum.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategory();
    }
}
