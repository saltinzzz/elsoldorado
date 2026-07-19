# Prueba manual de la V6

## Preparación

1. Crea `.env` desde `.env.example`.
2. Configura la contraseña real de PostgreSQL.
3. Verifica que exista la base `elsoldorado`.
4. Inicia el backend.
5. Confirma en la consola que Flyway aplicó `V5` y que Tomcat inició en el puerto 8080.
6. Inicia Angular en el puerto 4200.

## Pedido delivery

1. Inicia sesión como cliente.
2. Abre **Pedir**.
3. Agrega uno o más platos.
4. Selecciona **Delivery**.
5. Completa teléfono, dirección y distrito.
6. Usa el mapa, la ubicación actual o el marcador manual.
7. Selecciona un método de pago.
8. Confirma el pedido.
9. Debe aparecer un número de pedido y el total.

Comprueba en PostgreSQL:

```sql
SELECT id, nombre_cliente, tipo_entrega, direccion, distrito,
       latitud, longitud, metodo_pago, estado_pago, total
FROM pedido
ORDER BY id DESC;

SELECT id, pedido_id, nombre_plato, cantidad, precio_unitario, subtotal
FROM detalle_pedido
ORDER BY id DESC;
```

## Pedido para recojo

1. Selecciona **Recojo en local**.
2. Comprueba que desaparezcan dirección y mapa.
3. Selecciona una hora entre 12:00 y 21:30.
4. Confirma el pedido.
5. Verifica que `direccion`, `latitud` y `longitud` queden nulos.

## Reserva

1. Abre **Reservar**.
2. Usa una fecha y hora futuras.
3. Deja “Asignar automáticamente” o selecciona una mesa.
4. Confirma.
5. Comprueba la fila:

```sql
SELECT id, nombre_cliente, fecha, hora, cantidad_personas, mesa_id, estado
FROM reserva
ORDER BY id DESC;
```

## Panel

1. Ingresa como empleado o administrador.
2. Verifica que los datos carguen sin presionar otro botón.
3. Cambia el estado del pedido.
4. Cambia el estado del pago.
5. Cambia el estado de la reserva.
6. Abre la ubicación del delivery desde el panel.
7. Actualiza la página y confirma que los cambios permanezcan.

## Resultado esperado

- Ninguna pantalla queda indefinidamente en “Cargando…”.
- Los pedidos generan detalles relacionados.
- Las reservas generan una mesa válida.
- Los clientes solo ven sus operaciones.
- El personal ve las operaciones autorizadas.
- El mapa es opcional; la dirección manual permite continuar.
