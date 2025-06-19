# 🍕 Pizzamia - Backend

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Auth0](https://img.shields.io/badge/Auth0-EB5424?style=for-the-badge&logo=auth0&logoColor=white)

## 📝 Descripción

Pizzamia es un sistema de gestión para una pizzería que permite la administración de pedidos, productos, clientes y empleados. El sistema cuenta con una API REST completa para la integración con diferentes frontends y sistemas externos.

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database** (desarrollo)
- **Gradle**
- **Lombok**
- **OpenAPI/Swagger**

## 🔌 Integraciones

### 💳 MercadoPago

Integración completa con la API de Mercado Pago para procesar pagos de manera segura. Incluye:
- Creación de preferencias de pago

### ☁️ Cloudinary

Servicio de gestión de imágenes en la nube para:
- Almacenamiento de imágenes de productos
- Optimización automática de imágenes

### 🔐 Auth0

Sistema de autenticación y autorización:
- Login y registro de usuarios
- Control de roles y permisos
- Tokens JWT para acceso seguro a la API
- Gestión de roles en Auth0

## 🛠️ Instalación y Configuración

### Prerrequisitos

- JDK 17 o superior
- Gradle
- Git
- Cuenta en MercadoPago, Cloudinary y Auth0

### Pasos para ejecutar el proyecto

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/FrancoCastillo99/back-end-pizzamia.git
   cd back-end-pizzamia
   
2. **Configurar application-local.properties**

   Crea un archivo `application-local.properties` en `src/main/resources/` con el siguiente contenido:

   ```properties
   # Configuración de la base de datos H2
   spring.datasource.url=jdbc:h2:tcp://localhost/~/test
   spring.datasource.username=sa
   spring.datasource.password=

   # Configuración Cloudinary - Reemplaza con tus credenciales
   cloudinary.cloud-name=tu-cloud-name
   cloudinary.api-key=tu-api-key
   cloudinary.api-secret=tu-api-secret

   # Configuración de Mercado Pago - Reemplaza con tus credenciales
   mercadopago.access.token=tu-access-token
   mercadopago.webhook.secret=tu-webhook-secret

   # Auth0 Configuration - Reemplaza con tus credenciales
   spring.security.oauth2.resourceserver.jwt.issuer-uri=https://tu-dominio.auth0.com/
   auth0.audience=tu-audience
   auth0.domain=tu-dominio.auth0.com
   auth0.clientId=tu-client-id
   auth0.clientSecret=tu-client-secret

3. **Ejecutar la aplicación**
   ```bash
   ./gradlew bootRun
4. **Acceder a la aplicación**
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - Consola H2: `http://localhost:8080/h2-console`

## 📚 Documentación API

La documentación de la API está disponible a través de Swagger UI. Una vez que la aplicación esté en funcionamiento, puedes acceder a:

- Documentación OpenAPI: `http://localhost:8080/api-docs`
- Interfaz Swagger: `http://localhost:8080/swagger-ui.html`

## 👥 Equipo de Desarrollo

Este proyecto fue desarrollado por:

- **Franco Castillo** 
- **Lucas Chavez** 
- **Geronimo Crescitelli** 
- **Matias Rezinovsky** 
- **Nicolas Silva** 
