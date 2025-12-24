package br.one.forum.infra.spec;

import br.one.forum.entity.Category;
import br.one.forum.entity.Topic;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class TopicSpecification {

    public static Specification<Topic> byAuthorId(Long authorId) {
        return (root, query, cb) ->
                cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Topic> orderByMoreLiked() {
        return (root, query, cb) -> {

            if (query == null || !Topic.class.equals(query.getResultType())) {
                return null;
            }

            var likesJoin = root.join("likedBy", JoinType.LEFT);
            query.groupBy(root.get("id"));
            query.orderBy(cb.desc(cb.count(likesJoin.get("id"))));
            return cb.conjunction();
        };
    }

    public static Specification<Topic> byCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            Join<Topic, Category> categoriesJoin = root.join("categories", JoinType.INNER);
            return cb.equal(categoriesJoin.get("id"), categoryId);
        };
    }

    public static Specification<Topic> byTitleOrAuthorName(String titleOrAuthorName) {
        return (root, query, cb) -> {

            if (titleOrAuthorName == null || titleOrAuthorName.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + titleOrAuthorName.toLowerCase() + "%";


            var titlePredicate = cb.like(
                    cb.lower(root.get("title")),
                    pattern
            );


            var authorPredicate = cb.like(
                    cb.lower(root.join("author").get("profile").get("name")),
                    pattern
            );

            return cb.or(titlePredicate, authorPredicate);
        };
    }
}
