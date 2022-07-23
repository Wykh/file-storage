CREATE TABLE file_entity
(
    id                  VARCHAR(255) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    extension           VARCHAR(255) NOT NULL,
    comment             VARCHAR(255) NOT NULL,
    upload_date         TIMESTAMP,
    modified_date       TIMESTAMP,
    content_folder_path VARCHAR(255) NOT NULL,
    size                int8         NOT NULL,
    content             bytea,
    PRIMARY KEY (id)
)
