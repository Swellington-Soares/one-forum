package br.one.forum.repositories;

import br.one.forum.dtos.CategoryResponseDto;
import br.one.forum.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByNameIgnoreCase(String name);
    @Query("""
                 SELECT new br.one.forum.dtos.CategoryResponseDto(
                     c.id,
                     c.name,
                     COUNT(ct.id)
                 )
                 FROM Category c
                 LEFT JOIN c.topics ct
                 GROUP BY c.id, c.name
                 HAVING COUNT(ct.id) > 0
                 ORDER BY COUNT(ct.id)
            \s""")
    List<CategoryResponseDto> findAllWithTopicCount();
}