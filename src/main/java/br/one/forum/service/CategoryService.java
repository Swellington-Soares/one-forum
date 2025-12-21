package br.one.forum.service;


import br.one.forum.dto.response.CategoryResponseDto;
import br.one.forum.entity.Category;
import br.one.forum.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createOrGetCategory(String name) {
        return categoryRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    public List<CategoryResponseDto> getAllCategory() {
        return categoryRepository.findAllWithTopicCount()
                .stream()
                .sorted(Comparator.comparingLong(CategoryResponseDto::topicCount))
                .toList();

    }
}
