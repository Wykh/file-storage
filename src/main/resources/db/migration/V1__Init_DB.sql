CREATE TABLE file_entity
(
    id                  VARCHAR(255) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    extension           VARCHAR(255) NOT NULL,
    comment             VARCHAR(255) NOT NULL,
    upload_date         TIMESTAMP WITH TIME ZONE,
    modified_date       TIMESTAMP WITH TIME ZONE,
    content_folder_path VARCHAR(255) NOT NULL,
    size                int8         NOT NULL,
    PRIMARY KEY (id)
)
