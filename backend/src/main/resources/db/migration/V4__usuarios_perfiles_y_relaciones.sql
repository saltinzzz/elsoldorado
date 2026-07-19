CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP,
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMIN', 'EMPLEADO', 'CLIENTE'))
);

-- Convierte la antigua tabla cliente en un perfil relacionado con usuario.
ALTER TABLE cliente ADD COLUMN usuario_id BIGINT;
ALTER TABLE cliente ADD COLUMN direccion VARCHAR(200);

-- Preserva cualquier cliente de versiones anteriores asignándole una cuenta migrada.
INSERT INTO usuario (nombres, apellidos, email, password_hash, rol, activo)
SELECT COALESCE(NULLIF(TRIM(nombre), ''), 'Cliente'),
       'Migrado',
       'cliente-legacy-' || id || '@elsoldorado.local',
       '$2a$10$NEyxuw89BzFAg4fJ0jFJQuIl..RKxfx.o6Ar3qn0FDhXiInAEgecS',
       'CLIENTE',
       TRUE
FROM cliente;

UPDATE cliente c
SET usuario_id = u.id
FROM usuario u
WHERE u.email = 'cliente-legacy-' || c.id || '@elsoldorado.local';

ALTER TABLE cliente ALTER COLUMN usuario_id SET NOT NULL;
ALTER TABLE cliente ADD CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE;
ALTER TABLE cliente ADD CONSTRAINT uk_cliente_usuario UNIQUE (usuario_id);
ALTER TABLE cliente DROP COLUMN nombre;
ALTER TABLE cliente DROP COLUMN email;

CREATE TABLE empleado (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuario(id) ON DELETE CASCADE,
    cargo VARCHAR(80) NOT NULL,
    fecha_contratacion DATE
);

ALTER TABLE pedido ADD COLUMN usuario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL;
ALTER TABLE reserva ADD COLUMN usuario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL;

CREATE INDEX idx_usuario_rol_activo ON usuario(rol, activo);
CREATE INDEX idx_pedido_usuario ON pedido(usuario_id);
CREATE INDEX idx_reserva_usuario ON reserva(usuario_id);

-- Cuentas de demostración almacenadas en PostgreSQL. Las contraseñas están cifradas con BCrypt.
INSERT INTO usuario (nombres, apellidos, email, password_hash, rol, activo)
VALUES
('Administrador', 'General', 'admin@elsoldorado.pe', '$2a$10$VsWExQ.UcYj6LNxcbGZc1ONVDcwI15yBpyMNZ0qjJDrjZsT0jL1JS', 'ADMIN', TRUE),
('Mathias', 'Empleado', 'empleado@elsoldorado.pe', '$2a$10$PaN1eRvoY816Xv.wi1WhS.qY1oZAPS3m6EvVq5KM7J84DcDMmlTUG', 'EMPLEADO', TRUE),
('Cliente', 'Demostración', 'cliente@elsoldorado.pe', '$2a$10$NEyxuw89BzFAg4fJ0jFJQuIl..RKxfx.o6Ar3qn0FDhXiInAEgecS', 'CLIENTE', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO empleado (usuario_id, cargo, fecha_contratacion)
SELECT id, 'Atención al cliente', CURRENT_DATE FROM usuario WHERE email = 'empleado@elsoldorado.pe'
ON CONFLICT (usuario_id) DO NOTHING;

INSERT INTO cliente (usuario_id, telefono, direccion)
SELECT id, '987654321', 'Trujillo, La Libertad' FROM usuario WHERE email = 'cliente@elsoldorado.pe'
ON CONFLICT (usuario_id) DO NOTHING;
