package br.one.forum.services;

import br.one.forum.dtos.response.CategoryResponseDto;
import br.one.forum.entities.Category;
import br.one.forum.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createOrGetCategory(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    public List<CategoryResponseDto> getAllCategory() {
        return categoryRepository.findAllWithTopicCount();
    }
}
