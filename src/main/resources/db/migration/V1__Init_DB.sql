CREATE TABLE file_entity
(
    id                  uuid NOT NULL,
    name                VARCHAR(255) NOT NULL,
    extension           VARCHAR(255) NOT NULL,
    comment             VARCHAR(255) NOT NULL,
    upload_date         TIMESTAMP WITH TIME ZONE NOT NULL ,
    modified_date       TIMESTAMP WITH TIME ZONE NOT NULL ,
    content_folder_path VARCHAR(255) NOT NULL,
    size                int8         NOT NULL,
    PRIMARY KEY (id)
)
