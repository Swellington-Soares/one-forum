CREATE TABLE category
(
    id   INT          NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE category_has_topic
(
    category_id INT NOT NULL,
    topic_id   INT NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (category_id, topic_id)
);

CREATE TABLE comments
(
    id          INT NOT NULL,
    topic_id  INT                     NOT NULL,
    user_id    INT                     NOT NULL,
    content    LONGTEXT                NOT NULL,
    created_at timestamp DEFAULT NOW() NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE likes
(
    topic_id INT NOT NULL,
    user_id   INT NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (topic_id, user_id)
);

CREATE TABLE profile
(
    user_id INT      NOT NULL,
    name    LONGTEXT NOT NULL,
    photo   LONGTEXT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (user_id)
);

CREATE TABLE topic
(
    id         INT                     NOT NULL,
    title      LONGTEXT                NOT NULL,
    content    LONGTEXT                NOT NULL,
    user_id    INT                     NOT NULL,
    created_at timestamp DEFAULT NOW() NULL,
    updated_at timestamp DEFAULT NOW() NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE user
(
    id         INT                     NOT NULL,
    email      VARCHAR(255)            NULL,
    password   VARCHAR(64)             NOT NULL,
    created_at timestamp DEFAULT NOW() NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT email_UNIQUE UNIQUE (email);

ALTER TABLE category_has_topic
    ADD CONSTRAINT fk_category_has_topic_category1 FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE;

CREATE INDEX fk_category_has_topic_category1_idx ON category_has_topic (category_id);

ALTER TABLE category_has_topic
    ADD CONSTRAINT fk_category_has_topic_topic1 FOREIGN KEY (topic_id) REFERENCES topic (id) ON DELETE CASCADE;

CREATE INDEX fk_category_has_topic_topic1_idx ON category_has_topic (topic_id);

ALTER TABLE profile
    ADD CONSTRAINT fk_profile_user1 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION;

ALTER TABLE likes
    ADD CONSTRAINT fk_topic_has_user_topic1 FOREIGN KEY (topic_id) REFERENCES topic (id) ON DELETE CASCADE;

CREATE INDEX fk_topic_has_user_topic1_idx ON likes (topic_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_topic_has_user_topic2 FOREIGN KEY (topic_id) REFERENCES topic (id) ON DELETE CASCADE;

CREATE INDEX fk_topic_has_user_topic2_idx ON comments (topic_id);

ALTER TABLE likes
    ADD CONSTRAINT fk_topic_has_user_user1 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE;

CREATE INDEX fk_topic_has_user_user1_idx ON likes (user_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_topic_has_user_user2 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE;

CREATE INDEX fk_topic_has_user_user2_idx ON comments (user_id);

ALTER TABLE topic
    ADD CONSTRAINT fk_topic_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION;

CREATE INDEX fk_topic_user_idx ON topic (user_id);