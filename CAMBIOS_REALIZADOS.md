# Cambios realizados — El Sol Dorado V6

## 1. Correcciones funcionales

- Los selectores de cliente y mesa usan `null` mediante `[ngValue]`, evitando que Angular envíe `"0"` como identificador válido.
- Se corrigió la asociación `Pedido` ↔ `DetallePedido` con `mappedBy`, cascada, eliminación de huérfanos y método de asociación bidireccional.
- El backend recalcula los precios y el total usando los platos almacenados en PostgreSQL.
- Se validan cantidades, disponibilidad, teléfono, modalidad, dirección, distrito y horario de recojo.
- Las reservas validan fecha futura, horario, capacidad, cruces de 90 minutos y duplicidad.
- Los mensajes reales del backend se muestran en los formularios.

## 2. Actualización reactiva del frontend

- Se agregó notificación central de actualización después de las respuestas HTTP para Angular sin ZoneJS.
- Los componentes principales usan `ChangeDetectorRef` y `finalize()` para cerrar estados de carga.
- Inicio, panel, pedidos y reservas actualizan la interfaz sin requerir un clic adicional.
- Se agregaron estados visuales de carga, error, vacío y éxito.

## 3. Delivery y recojo

- El pedido admite exclusivamente `DELIVERY` y `RECOJO_LOCAL`.
- Delivery incluye dirección, distrito, referencia, latitud y longitud.
- Recojo incluye hora aproximada, entre 12:00 y 21:30.
- Se eliminó “consumo en local” del pedido; la atención presencial se gestiona mediante reservas.

## 4. Ubicación

- Integración de Leaflet con OpenStreetMap.
- Botón para usar la ubicación actual del navegador.
- Búsqueda textual de dirección mediante Nominatim.
- Geocodificación inversa al mover el marcador.
- Marcador arrastrable y selección mediante clic.
- Dirección manual disponible como respaldo.
- El mapa se destruye y recrea correctamente al cambiar entre delivery y recojo.
- El panel permite abrir las coordenadas del pedido en OpenStreetMap.

## 5. Pago simulado

- Métodos: efectivo, tarjeta al recibir, transferencia, Yape y Plin.
- Estados: pendiente, pagado y anulado.
- Fecha de pago y código de operación opcional.
- No se procesa dinero ni se integra una pasarela real.

## 6. Base de datos

Nueva migración:

```text
V5__delivery_pago_y_detalle_pedido.sql
```

Agrega a `pedido`:

- `tipo_entrega`;
- `distrito`;
- `latitud` y `longitud`;
- `hora_recojo`;
- `observacion`;
- `metodo_pago`;
- `estado_pago`;
- `fecha_pago`;
- `codigo_operacion`.

Incluye restricciones e índices. Las migraciones `V1` a `V4` permanecen intactas.

## 7. Página pública y navegación

- Nueva portada gastronómica.
- Secciones de historia, destacados, servicios, carta, testimonios referenciales y contacto.
- Encabezado fijo con navegación según sesión y rol.
- Menú de cuenta con perfil y cierre de sesión.
- Footer completo sin referencias visibles a un proyecto académico.

## 8. Panel de gestión

- Tarjetas de resumen: pedidos del día, pendientes, reservas, mesas, usuarios y ventas registradas.
- Últimos pedidos y próximas reservas.
- Filtros de pedidos, reservas y usuarios.
- Visualización de modalidad, dirección, pago y estado.
- Orden correcto de próximas reservas y cálculo de “hoy” con fecha local.
- Acciones rápidas y menú lateral mejorado.

## 9. Formularios y presentación

- Formularios organizados por secciones.
- Validaciones visibles y botones bloqueados durante el envío.
- Confirmaciones de éxito y error.
- Skeleton loaders.
- Tarjetas de platos, etiquetas y estados visuales.
- Paleta marrón, dorado, crema y terracota.

## 10. Pruebas añadidas

- Verificación backend de recojo local sin dirección.
- Verificación de asociación del detalle con su pedido.
- Verificación frontend de selectores inicializados en `null`.
- Verificación de que las únicas modalidades sean delivery y recojo.
