DROP TABLE IF EXISTS courses;

CREATE TABLE url (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    created_at TIMESTAMP
);