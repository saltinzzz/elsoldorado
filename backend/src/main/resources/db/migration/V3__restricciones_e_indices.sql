ALTER TABLE plato ALTER COLUMN categoria_id SET NOT NULL;
ALTER TABLE detalle_pedido ALTER COLUMN pedido_id SET NOT NULL;

ALTER TABLE plato ADD CONSTRAINT chk_plato_precio_positivo CHECK (precio > 0);
ALTER TABLE mesa ADD CONSTRAINT chk_mesa_numero_positivo CHECK (numero > 0);
ALTER TABLE mesa ADD CONSTRAINT chk_mesa_capacidad_positiva CHECK (capacidad > 0);
ALTER TABLE reserva ADD CONSTRAINT chk_reserva_personas_positivas CHECK (cantidad_personas > 0);
ALTER TABLE pedido ADD CONSTRAINT chk_pedido_total_no_negativo CHECK (total IS NULL OR total >= 0);
ALTER TABLE detalle_pedido ADD CONSTRAINT chk_detalle_cantidad_positiva CHECK (cantidad > 0);
ALTER TABLE detalle_pedido ADD CONSTRAINT chk_detalle_precio_no_negativo CHECK (precio_unitario >= 0 AND subtotal >= 0);

CREATE INDEX IF NOT EXISTS idx_plato_categoria ON plato(categoria_id);
CREATE INDEX IF NOT EXISTS idx_reserva_fecha_hora ON reserva(fecha, hora);
CREATE INDEX IF NOT EXISTS idx_reserva_cliente ON reserva(nombre_cliente);
CREATE INDEX IF NOT EXISTS idx_reserva_mesa ON reserva(mesa_id);
CREATE INDEX IF NOT EXISTS idx_pedido_fecha_hora ON pedido(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_pedido_cliente ON pedido(nombre_cliente);
CREATE INDEX IF NOT EXISTS idx_detalle_pedido ON detalle_pedido(pedido_id);
