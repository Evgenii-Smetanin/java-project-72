DROP TABLE IF EXISTS url_check;
DROP TABLE IF EXISTS url;

CREATE TABLE url (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE url_check (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status_code INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description VARCHAR(500),
    url_id INT,
    created_at TIMESTAMP,
    FOREIGN KEY (url_id) REFERENCES url(id)
);