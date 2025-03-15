-- !Ups
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE todo (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL,
    priority VARCHAR(50) NOT NULL,
    deadline DATE,
    categoryId BIGINT,
    FOREIGN KEY (categoryId) REFERENCES category(id)
);

-- !Downs
DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS category;