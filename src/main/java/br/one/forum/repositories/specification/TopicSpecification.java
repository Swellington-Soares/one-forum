package br.one.forum.repositories.specification;

import br.one.forum.entities.Topic;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;



public class TopicSpecification {

    public static Specification<Topic> byAuthor(Long authorId){
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), authorId);
    }

    public static Specification<Topic> orderByMoreLiked(){
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            var join = root.join("likedBy", JoinType.LEFT);
            query.groupBy(root.get("id"));
            query.orderBy(cb.desc(cb.count(join)));
            return cb.conjunction();
        };
    }
}
