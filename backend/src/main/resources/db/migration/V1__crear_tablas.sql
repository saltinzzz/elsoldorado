CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE plato (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    destacado BOOLEAN DEFAULT FALSE,
    visible_en_inicio BOOLEAN DEFAULT FALSE,
    categoria_id BIGINT REFERENCES categoria(id)
);

CREATE TABLE mesa (
    id BIGSERIAL PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE reserva (
    id BIGSERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    cantidad_personas INT NOT NULL,
    observacion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    mesa_id BIGINT REFERENCES mesa(id)
);

CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    referencia VARCHAR(200),
    total DECIMAL(10,2),
    fecha_hora TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
);

CREATE TABLE detalle_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES pedido(id) ON DELETE CASCADE,
    id_plato BIGINT NOT NULL,
    nombre_plato VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);