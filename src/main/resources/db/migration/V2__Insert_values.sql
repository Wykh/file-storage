INSERT INTO role_entity (role) VALUES ('ADMIN');
INSERT INTO role_entity (role) VALUES ('USER');
INSERT INTO role_entity (role) VALUES ('MANAGER');

INSERT INTO user_entity (name, password, role_id) VALUES ('admin', 'admin', 1);
INSERT INTO user_entity (name, password, role_id) VALUES ('irina', '321', 1);
INSERT INTO user_entity (name, password, role_id) VALUES ('atrem', '123', 2);
INSERT INTO user_entity (name, password, role_id) VALUES ('maxim', '123', 2);
INSERT INTO user_entity (name, password, role_id) VALUES ('sasha', '123', 2);