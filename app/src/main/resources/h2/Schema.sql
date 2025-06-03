DROP TABLE IF EXISTS courses;

CREATE TABLE courses
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255),
    created_at TIMESTAMP
);