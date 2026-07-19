# Validación técnica — El Sol Dorado V6

Fecha: 6 de julio de 2026.

## Backend

Comando ejecutado:

```bash
mvn test
```

Resultado:

- 72 archivos Java compilados.
- **11 pruebas ejecutadas**.
- 0 fallos.
- 0 errores.
- Resultado: **BUILD SUCCESS**.

Cobertura principal:

- utilidades de seguridad;
- menú y disponibilidad;
- usuarios y contraseñas;
- cálculo de pedidos;
- relación pedido-detalle;
- delivery y recojo;
- validaciones de reservas y mesas.

## Frontend

Comandos ejecutados:

```bash
npm test -- --watch=false
npm run build
```

Resultado:

- 12 archivos de prueba aprobados;
- **14 pruebas aprobadas**;
- build de producción generado correctamente;
- sin errores de TypeScript ni de plantillas Angular;
- tamaño inicial de producción aproximado: 760 kB sin comprimir.

## Migraciones

- `V1`, `V2`, `V3` y `V4` no fueron modificadas.
- Se añadió `V5__delivery_pago_y_detalle_pedido.sql`.
- El esquema contempla ambas modalidades, pago simulado y coordenadas opcionales.

## Qué sí fue validado automáticamente

- Compilación del backend.
- Compilación del frontend.
- Servicios y lógica unitaria.
- Formularios inicializados sin IDs inválidos.
- Build de producción.

## Qué debe validarse en la computadora del usuario

El entorno de validación no tenía un servidor PostgreSQL disponible. Por ello, realiza la prueba manual con tu base local:

1. aplicar migraciones Flyway;
2. registrar pedido delivery;
3. comprobar fila en `pedido`;
4. comprobar filas relacionadas en `detalle_pedido`;
5. registrar pedido para recojo;
6. registrar reserva;
7. cambiar estados desde el panel;
8. probar ubicación y mapa con internet.

El procedimiento exacto está en `PRUEBA_MANUAL.md`.
