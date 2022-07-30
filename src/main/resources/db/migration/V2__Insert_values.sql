INSERT INTO role_entity (name) VALUES ('ADMIN');
INSERT INTO role_entity (name) VALUES ('USER');
INSERT INTO role_entity (name) VALUES ('MANAGER');

INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('admin', 'admin', 1, FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('irina', '321', 1, FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('atrem', '123', 2, FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('maxim', '123', 2, FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('sasha', '123', 2, FALSE);