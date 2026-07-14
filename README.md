# Contatodo Backend

Backend para el sistema de registro de compras y ventas. API REST construida con Spring Boot 3.2.5, Java 17, MongoDB, y autenticación JWT.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data MongoDB**
- **Spring Security** con JWT
- **Maven**

## Estructura del Proyecto

```
contatodo/
├── src/
│   ├── main/
│   │   ├── java/com/contatodo/
│   │   └── resources/
│   └── test/
├── deploy/
│   ├── deploy-ec2.sh
│   └── docker-compose.ec2.yml
├── .github/
│   └── workflows/
│       ├── backend-pipeline.yml
│       └── deploy.yml
├── Dockerfile
├── pom.xml
└── DOCKER.md
```

## Desarrollo

### Prerrequisitos

- Java 17
- Maven 3.6+
- MongoDB (local o Docker)

### Ejecutar Localmente

```bash
# Clonar el repositorio
cd contatodo

# Configurar variables de entorno
cp .env.local .env

# Ejecutar con Maven
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

### Ejecutar Tests

```bash
mvn test
```

### Construir JAR

```bash
mvn clean package
```

El JAR se generará en `target/contatodo-1.0.0.jar`

## Docker

Para información detallada sobre Docker, consulta [DOCKER.md](DOCKER.md)

### Construir Imagen

```bash
docker build -t contatodo-backend .
```

### Ejecutar con Docker Compose

```bash
# Desde el directorio raíz del proyecto
docker-compose up -d
```

Esto inicia:
- MongoDB en puerto 27017
- Backend en puerto 8080

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `APP_NAME` | Nombre de la aplicación | `Contatodo` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `MONGO_URI` | URI de conexión MongoDB | `mongodb://localhost:27017/` |
| `MONGO_DATABASE` | Nombre de base de datos MongoDB | `contatodo` |
| `JWT_SECRET` | Clave secreta para JWT | `mySecretKeyForJWTTokenGeneration123456789` |
| `JWT_EXPIRATION` | Tiempo de expiración JWT (ms) | `86400000` |
| `BCRYPT_STRENGTH` | Fuerza de encriptación BCrypt | `12` |

## CI/CD

### Pipeline de Backend

El workflow `.github/workflows/backend-pipeline.yml` ejecuta:

1. **CI** (todas las ramas):
   - Compilación con Maven
   - Ejecución de tests
   - Empaquetado del JAR
   - Verificación de build Docker

2. **CD** (solo master):
   - Build y push de imagen Docker a GitHub Container Registry
   - Tagging: `{branch}-{sha}` y `latest` para master

### Despliegue Automático

El workflow `.github/workflows/deploy.yml` se ejecuta después del CD exitoso en master:

- Resuelve la referencia de la imagen Docker
- Genera archivo `.env` con configuración
- Sube archivos a EC2 via SSH
- Ejecuta script de despliegue remoto
- Reinicia contenedores con la nueva imagen

## Despliegue a Producción

### GitHub Secrets Requeridos

Configura estos en el entorno `master` de GitHub:

**Secrets:**
- `SSH_PRIVATE_KEY` - Clave privada SSH para EC2
- `MONGO_URI` - URI de MongoDB en producción
- `JWT_SECRET` - Clave secreta JWT para producción

**Variables (secrets o variables):**
- `APP_NAME` - Nombre de la aplicación
- `SERVER_PORT` - Puerto del servidor
- `MONGO_DATABASE` - Nombre de base de datos
- `JWT_EXPIRATION` - Expiración JWT
- `BCRYPT_STRENGTH` - Fuerza BCrypt

**Variables de entorno (opcionales):**
- `SSH_HOST` - Host/IP de EC2
- `SSH_USER` - Usuario SSH
- `SSH_PORT` - Puerto SSH (default: 22)
- `EC2_APP_DIR` - Directorio en EC2 (default: `/opt/contatodo`)

### Imagen Docker en Producción

La imagen se publica en GitHub Container Registry:

```text
ghcr.io/davichus1303/contatodo/contatodo:master-<short-sha>
ghcr.io/davichus1303/contatodo/contatodo:latest
```

### Red Docker

El backend se conecta a la red Docker `contatodo-network` para comunicación con el frontend.

## API Endpoints

### Autenticación

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario

### Compras

- `GET /api/compras` - Listar compras
- `POST /api/compras` - Crear compra
- `GET /api/compras/{id}` - Obtener compra por ID
- `PUT /api/compras/{id}` - Actualizar compra
- `DELETE /api/compras/{id}` - Eliminar compra

### Ventas

- `GET /api/ventas` - Listar ventas
- `POST /api/ventas` - Crear venta
- `GET /api/ventas/{id}` - Obtener venta por ID
- `PUT /api/ventas/{id}` - Actualizar venta
- `DELETE /api/ventas/{id}` - Eliminar venta

## Seguridad

- Autenticación JWT basada en tokens
- Encriptación de contraseñas con BCrypt
- Configuración de Spring Security para endpoints protegidos
- CORS configurado para comunicación con frontend

## Troubleshooting

### El contenedor falla al iniciar

```bash
docker logs contatodo-backend
```

### Problemas de conexión MongoDB

Asegúrate de que MongoDB esté ejecutándose y accesible. En Docker Compose, el backend se conecta usando el nombre del servicio `mongodb` como hostname.

### Conflictos de puerto

Si el puerto 8080 está en uso, modifica el mapeo de puertos en `docker-compose.yml` o al ejecutar el contenedor manualmente.

## Contribución

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT.
