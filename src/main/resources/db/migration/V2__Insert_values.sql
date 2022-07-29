INSERT INTO role_entity (role) VALUES ('ADMIN');
INSERT INTO role_entity (role) VALUES ('USER');

INSERT INTO user_entity (name, password, role_id) VALUES ('admin', 'admin', 1);
INSERT INTO user_entity (name, password, role_id) VALUES ('user', 'user', 2);