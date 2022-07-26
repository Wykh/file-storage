
ALTER TABLE file_entity
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE file_entity
    ALTER COLUMN comment SET DEFAULT '';

ALTER TABLE file_entity
    ALTER COLUMN upload_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE file_entity
    ALTER COLUMN modified_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE file_entity
    ALTER COLUMN content_folder_path SET DEFAULT 'upload-dir';

ALTER TABLE file_entity
    ALTER COLUMN size SET DEFAULT 0;

