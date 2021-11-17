CREATE TABLE IF NOT EXISTS post (
    id SERIAL PRIMARY KEY,
    name TEXT,
    description TEXT,
    creation_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS city (
    id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE IF NOT EXISTS candidate (
    id SERIAL PRIMARY KEY,
    name TEXT,
    city_id INT REFERENCES city(id),
    creation_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name TEXT,
    email TEXT,
    password TEXT
);

INSERT INTO city VALUES
    (1, 'Москва'),
    (2, 'Санкт-Петербург'),
    (3, 'Казань'),
    (4, 'Нижний Новгород'),
    (5, 'Владивосток');