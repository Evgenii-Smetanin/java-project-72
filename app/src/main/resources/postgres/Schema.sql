DROP TABLE IF EXISTS url_check;
DROP TABLE IF EXISTS url;

CREATE TABLE url (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE url_check (
    id SERIAL PRIMARY KEY,
    status_code INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description VARCHAR(500),
    url_id INT REFERENCES url(id),
    created_at TIMESTAMP
);