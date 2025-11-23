package br.one.forum.controllers;

import br.one.forum.dtos.CategoryResponseDto;
import br.one.forum.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategory();
    }
}
