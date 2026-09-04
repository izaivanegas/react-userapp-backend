-- ============================================
-- data.sql - DATOS DE PRUEBA
-- ============================================

-- 1. VERIFICACIÓN: Saber si el script se ejecuta
SELECT '✅ DATA.SQL EJECUTADO CORRECTAMENTE' AS STATUS;

-- 2. INSERTAR ROLES (primero los roles)
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_ADMIN');
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_MANAGER');

-- 3. INSERTAR USUARIOS
INSERT IGNORE INTO users (username, password, email) VALUES
('juan.perez', '$2a$10$eVWdAgZ6NcStr1jaf5hn/OuZVaBCP1qnXpL0udxjbxSM3fbv5LVzO', 'juan.perez@email.com');

INSERT IGNORE INTO users (username, password, email) VALUES
('maria.garcia', '$2a$10$eVWdAgZ6NcStr1jaf5hn/OuZVaBCP1qnXpL0udxjbxSM3fbv5LVzO', 'maria.garcia@email.com');

INSERT IGNORE INTO users (username, password, email) VALUES
('carlos.lopez', '$2a$10$eVWdAgZ6NcStr1jaf5hn/OuZVaBCP1qnXpL0udxjbxSM3fbv5LVzO', 'carlos.lopez@email.com');

INSERT IGNORE INTO users (username, password, email) VALUES
('ana.martinez', '$2a$10$eVWdAgZ6NcStr1jaf5hn/OuZVaBCP1qnXpL0udxjbxSM3fbv5LVzO', 'ana.martinez@email.com');

-- 4. ASIGNAR ROLES A USUARIOS
-- Nota: Los IDs pueden variar, por eso usamos subconsultas
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'juan.perez' AND r.nombre = 'ROLE_ADMIN';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'juan.perez' AND r.nombre = 'ROLE_USER';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'maria.garcia' AND r.nombre = 'ROLE_USER';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'carlos.lopez' AND r.nombre = 'ROLE_USER';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'carlos.lopez' AND r.nombre = 'ROLE_MANAGER';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'ana.martinez' AND r.nombre = 'ROLE_USER';

-- 5. CONFIRMACIÓN FINAL
SELECT '✅ DATOS CARGADOS EXITOSAMENTE' AS FINAL;