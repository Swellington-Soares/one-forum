-- Remove foreign keys (usando IF EXISTS para evitar erros)
SET FOREIGN_KEY_CHECKS = 0;

-- Remover constraints se existirem
ALTER TABLE comments DROP FOREIGN KEY IF EXISTS FK_COMMENTS_ON_AUTHOR;
ALTER TABLE comments DROP FOREIGN KEY IF EXISTS FK_COMMENTS_ON_TOPIC;
ALTER TABLE comments DROP FOREIGN KEY IF EXISTS FK6kufofduwwmots3cxumjvsve7;
ALTER TABLE comments DROP FOREIGN KEY IF EXISTS FKhklwekhy0lasxveqxmbtkjqpn;
ALTER TABLE topics DROP FOREIGN KEY IF EXISTS FK_TOPICS_ON_AUTHOR;
ALTER TABLE topics DROP FOREIGN KEY IF EXISTS FK7xkxef4fwbjec06isf4ims4gr;
ALTER TABLE category_has_topic DROP FOREIGN KEY IF EXISTS fk_cathastop_on_category;
ALTER TABLE category_has_topic DROP FOREIGN KEY IF EXISTS fk_cathastop_on_topic;
ALTER TABLE category_has_topic DROP FOREIGN KEY IF EXISTS FK1ltfbr3kk11m922dj1rlelqk5;
ALTER TABLE category_has_topic DROP FOREIGN KEY IF EXISTS FK57tpywixvw42q02uu3v3xbgvm;
ALTER TABLE likes DROP FOREIGN KEY IF EXISTS fk_likes_on_topic;
ALTER TABLE likes DROP FOREIGN KEY IF EXISTS fk_likes_on_user;
ALTER TABLE likes DROP FOREIGN KEY IF EXISTS FK2qdmhh14l4a4u9r8r4v69n0wm;
ALTER TABLE likes DROP FOREIGN KEY IF EXISTS FKnvx9seeqqyy71bij291pwiwrg;

-- Convert all ID columns to BIGINT
ALTER TABLE categories MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;
ALTER TABLE topics MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;
ALTER TABLE comments MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;

-- Convert all foreign key columns to BIGINT
ALTER TABLE category_has_topic MODIFY COLUMN category_id BIGINT NOT NULL;
ALTER TABLE category_has_topic MODIFY COLUMN topic_id BIGINT NOT NULL;
ALTER TABLE comments MODIFY COLUMN topic_id BIGINT NOT NULL;
ALTER TABLE comments MODIFY COLUMN author_id BIGINT NOT NULL;
ALTER TABLE likes MODIFY COLUMN topic_id BIGINT NOT NULL;
ALTER TABLE likes MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE topics MODIFY COLUMN author_id BIGINT NOT NULL;

-- Recreate foreign keys
ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE category_has_topic
    ADD CONSTRAINT fk_cathastop_on_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE category_has_topic
    ADD CONSTRAINT fk_cathastop_on_topic FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE likes
    ADD CONSTRAINT fk_likes_on_topic FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE likes
    ADD CONSTRAINT fk_likes_on_user FOREIGN KEY (user_id) REFERENCES users (id);

SET FOREIGN_KEY_CHECKS = 1;
