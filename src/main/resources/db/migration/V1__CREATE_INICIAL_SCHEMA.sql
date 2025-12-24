CREATE TABLE categories
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE category_has_topic
(
    category_id INT NOT NULL,
    topic_id    INT NOT NULL,
    CONSTRAINT pk_category_has_topic PRIMARY KEY (category_id, topic_id)
);

CREATE TABLE comments
(
    id         INT AUTO_INCREMENT NOT NULL,
    created_at datetime           NULL,
    updated_at datetime           NULL,
    topic_id   INT                NOT NULL,
    author_id  BIGINT             NOT NULL,
    content    VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE likes
(
    topic_id INT    NOT NULL,
    user_id  BIGINT NOT NULL,
    CONSTRAINT pk_likes PRIMARY KEY (topic_id, user_id)
);

CREATE TABLE tokens
(
    id         BIGINT AUTO_INCREMENT                  NOT NULL,
    created_at datetime                               NULL,
    updated_at datetime                               NULL,
    email      VARCHAR(60)                            NOT NULL,
    token      VARCHAR(255)                           NOT NULL,
    type       ENUM ('EMAIL_TOKEN', 'PASSWORD_TOKEN') NULL,
    expiration datetime                               NOT NULL,
    CONSTRAINT pk_tokens PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id         INT AUTO_INCREMENT NOT NULL,
    created_at datetime           NULL,
    updated_at datetime           NULL,
    title      VARCHAR(255)       NOT NULL,
    content    LONGTEXT           NOT NULL,
    author_id  BIGINT             NOT NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                       BIGINT AUTO_INCREMENT NOT NULL,
    created_at               datetime              NULL,
    updated_at               datetime              NULL,
    email                    VARCHAR(60)           NOT NULL,
    password                 VARCHAR(255)          NOT NULL,
    email_verified           BIT(1)                NOT NULL DEFAULT FALSE,
    `locked`                 BIT(1)                NOT NULL DEFAULT FALSE,
    deleted                  BIT(1)                NOT NULL DEFAULT FALSE,
    refresh_token            VARCHAR(255)          NULL,
    refresh_token_expiration datetime              NULL,
    name                     VARCHAR(255)          NOT NULL,
    photo                    LONGTEXT              NULL,
    bio                      LONGTEXT              NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);

ALTER TABLE tokens
    ADD CONSTRAINT uc_tokens_token UNIQUE (token);

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