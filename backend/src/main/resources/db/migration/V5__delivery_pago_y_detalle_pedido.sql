ALTER TABLE pedido ALTER COLUMN direccion DROP NOT NULL;

ALTER TABLE pedido ADD COLUMN tipo_entrega VARCHAR(20) NOT NULL DEFAULT 'DELIVERY';
ALTER TABLE pedido ADD COLUMN distrito VARCHAR(80);
ALTER TABLE pedido ADD COLUMN latitud DECIMAL(10,7);
ALTER TABLE pedido ADD COLUMN longitud DECIMAL(10,7);
ALTER TABLE pedido ADD COLUMN hora_recojo TIME;
ALTER TABLE pedido ADD COLUMN observacion TEXT;
ALTER TABLE pedido ADD COLUMN metodo_pago VARCHAR(20) NOT NULL DEFAULT 'EFECTIVO';
ALTER TABLE pedido ADD COLUMN estado_pago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE';
ALTER TABLE pedido ADD COLUMN fecha_pago TIMESTAMP;
ALTER TABLE pedido ADD COLUMN codigo_operacion VARCHAR(80);

-- Compatibilidad con pedidos creados en versiones anteriores.
UPDATE pedido SET distrito = 'Trujillo' WHERE tipo_entrega = 'DELIVERY' AND distrito IS NULL;

ALTER TABLE pedido ADD CONSTRAINT chk_pedido_tipo_entrega
    CHECK (tipo_entrega IN ('DELIVERY', 'RECOJO_LOCAL'));
ALTER TABLE pedido ADD CONSTRAINT chk_pedido_metodo_pago
    CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'YAPE', 'PLIN'));
ALTER TABLE pedido ADD CONSTRAINT chk_pedido_estado_pago
    CHECK (estado_pago IN ('PENDIENTE', 'PAGADO', 'ANULADO'));
ALTER TABLE pedido ADD CONSTRAINT chk_pedido_datos_entrega
    CHECK (
        (tipo_entrega = 'DELIVERY' AND direccion IS NOT NULL AND LENGTH(TRIM(direccion)) >= 5 AND distrito IS NOT NULL AND LENGTH(TRIM(distrito)) >= 2)
        OR
        (tipo_entrega = 'RECOJO_LOCAL' AND hora_recojo IS NOT NULL)
    );
ALTER TABLE pedido ADD CONSTRAINT chk_pedido_coordenadas
    CHECK ((latitud IS NULL AND longitud IS NULL) OR (latitud IS NOT NULL AND longitud IS NOT NULL));

CREATE INDEX idx_pedido_tipo_entrega ON pedido(tipo_entrega);
CREATE INDEX idx_pedido_estado_pago ON pedido(estado_pago);
CREATE INDEX idx_pedido_fecha_estado ON pedido(fecha_hora, estado);
