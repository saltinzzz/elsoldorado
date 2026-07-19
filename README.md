# El Sol Dorado V6 — sistema de gestión para restaurante

Aplicación full stack para administrar usuarios, carta, categorías, mesas, pedidos y reservas del restaurante **El Sol Dorado**.

## Tecnologías

- **Backend:** Java 21, Spring Boot 3.5, Spring Security, JWT, BCrypt, Spring Data JPA, Flyway y PostgreSQL.
- **Frontend:** Angular 21, formularios reactivos, Bootstrap y Leaflet.
- **Mapa:** OpenStreetMap y geocodificación de Nominatim para una demostración académica de ubicación de delivery.

## Novedades de la V6

- Corrección de la creación de pedidos y reservas: los selectores ya no envían identificadores `0` inexistentes.
- Relación bidireccional correcta entre `pedido` y `detalle_pedido` para guardar el pedido completo en una misma transacción.
- Actualización inmediata de Angular después de respuestas HTTP; las pantallas ya no permanecen congeladas en “Cargando…”.
- Pedidos con dos modalidades claramente separadas:
  - **Delivery**.
  - **Recojo en local**.
- Dirección de delivery con ingreso manual, ubicación actual, búsqueda de dirección, mapa y marcador ajustable.
- Métodos de pago simulados: efectivo, tarjeta al recibir, transferencia, Yape y Plin.
- Estados de pago: pendiente, pagado y anulado.
- Página pública renovada con presentación, nosotros, destacados, servicios, carta, testimonios referenciales y contacto.
- Navegación y pie de página con apariencia de restaurante real.
- Panel de gestión renovado con indicadores, últimos pedidos, próximas reservas, filtros y acceso al mapa.
- Nueva migración Flyway `V5__delivery_pago_y_detalle_pedido.sql` sin modificar las migraciones anteriores.

## Arquitectura de usuarios

- `usuario`: identidad, correo, contraseña cifrada, rol, estado y auditoría.
- `cliente`: teléfono y dirección, vinculados uno a uno con `usuario`.
- `empleado`: cargo y fecha de contratación, vinculados uno a uno con `usuario`.
- Los administradores se identifican mediante el rol `ADMIN`, sin una tabla redundante.
- Los pedidos y reservas se relacionan con la cuenta del cliente y conservan el nombre como dato histórico.

## Cuentas iniciales para demostración

| Perfil | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@elsoldorado.pe` | `admin123` |
| Empleado | `empleado@elsoldorado.pe` | `empleado123` |
| Cliente | `cliente@elsoldorado.pe` | `cliente123` |

Las contraseñas se almacenan cifradas con BCrypt. Puedes cambiarlas desde el panel.

## 1. Base de datos

### Opción A: PostgreSQL instalado

Crea una base de datos vacía llamada:

```text
elsoldorado
```

No crees las tablas manualmente. Flyway ejecutará las migraciones `V1` a `V5`.

### Opción B: Docker

```bash
docker compose up -d
```

## 2. Variables de entorno

En PowerShell, desde la carpeta principal:

```powershell
Copy-Item .env.example .env
notepad .env
```

Actualiza al menos:

```properties
DB_PASSWORD=TU_CONTRASEÑA_REAL_DE_POSTGRESQL
JWT_SECRET=una-clave-larga-y-diferente-de-al-menos-32-caracteres
```

VS Code leerá `.env` mediante `.vscode/launch.json`.

## 3. Ejecutar el backend

Requisito: **JDK 21**.

Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
chmod +x mvnw
./mvnw clean test
./mvnw spring-boot:run
```

Backend:

```text
http://localhost:8080
```

## 4. Ejecutar el frontend

Requisitos: **Node.js 22** y npm.

```powershell
cd frontend
npm install
npm test -- --watch=false
npm start
```

Frontend:

```text
http://localhost:4200
```

## 5. Probar el mapa

- El mapa necesita conexión a internet para cargar Leaflet, OpenStreetMap y el servicio de geocodificación.
- En `localhost`, el navegador permite solicitar la ubicación actual.
- Si el usuario no concede permiso o el servicio externo no responde, el pedido puede completarse con dirección manual.
- No existe seguimiento del repartidor ni cálculo automático de rutas.

## Actualización desde V5

Si tu base ya tiene aplicadas las migraciones `V1`, `V2`, `V3` y `V4`, conserva esa base e inicia el backend: Flyway aplicará únicamente `V5`.

No edites ninguna migración que ya haya sido aplicada. Para cambios futuros crea `V6__...sql`, `V7__...sql`, etc.

## Flujo de prueba recomendado

1. Inicia PostgreSQL, backend y frontend.
2. Ingresa como cliente o registra una cuenta nueva.
3. Crea un pedido por delivery y selecciona un punto del mapa.
4. Crea otro pedido para recojo en local.
5. Registra una reserva con asignación automática de mesa.
6. Ingresa como empleado o administrador.
7. Revisa pedidos y reservas en el panel y cambia sus estados.
8. Marca un pago como pagado y registra un código de operación opcional.
9. Confirma en PostgreSQL los registros de `pedido`, `detalle_pedido` y `reserva`.

Consulta también `PRUEBA_MANUAL.md`, `CAMBIOS_REALIZADOS.md` y `VALIDACION.md`.

## Entrega limpia

El ZIP final no contiene `node_modules`, `dist`, `target`, `.env`, cachés ni repositorio Git interno. Ejecuta `npm install` y los comandos Maven en cada computadora.
