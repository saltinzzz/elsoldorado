INSERT INTO categoria (nombre) VALUES
('Entradas'),
('Platos de Fondo'),
('Bebidas'),
('Postres');

INSERT INTO plato (nombre, descripcion, precio, disponible, destacado, visible_en_inicio, categoria_id) VALUES
('Ceviche', 'Pescado fresco con limón y ají', 32.00, TRUE, TRUE, TRUE, 1),
('Papa a la Huancaína', 'Papa con crema de ají amarillo', 18.00, TRUE, TRUE, TRUE, 1),
('Lomo Saltado', 'Carne salteada con papas fritas y arroz', 28.00, TRUE, TRUE, TRUE, 2),
('Ají de Gallina', 'Pollo deshilachado en crema', 24.00, TRUE, FALSE, TRUE, 2),
('Chicha Morada', 'Bebida tradicional peruana', 8.00, TRUE, TRUE, TRUE, 3),
('Suspiro a la Limeña', 'Postre tradicional peruano', 14.00, TRUE, FALSE, FALSE, 4);

INSERT INTO mesa (numero, capacidad, disponible) VALUES
(1, 2, TRUE),
(2, 4, TRUE),
(3, 6, FALSE),
(4, 4, TRUE);
