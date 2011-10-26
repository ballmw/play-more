# Athletes schema

# --- !Ups

CREATE TABLE Athlete (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    firstName varchar(100) NOT NULL,
    lastName varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Athlete;