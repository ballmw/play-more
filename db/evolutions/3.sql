# --- !Ups

CREATE TABLE comment (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    author varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt date NOT NULL,
    workout_id bigint(20) NOT NULL,
    FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Comment;