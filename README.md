# 🚀 Pedidos360

Pedidos360 es una solución de comercio electrónico basada en una arquitectura de microservicios moderna y desacoplada en Spring Boot. El proyecto está organizado por dominios funcionales, separando la lógica de órdenes y catálogo para facilitar el mantenimiento, la escalabilidad y la evolución independiente de cada servicio.

La solución está pensada para operar en un entorno Cloud-native, con una base sólida para integrarse con AWS API Gateway y VPC Link, manteniendo una separación clara entre responsabilidades del negocio y la infraestructura.

---

## 🏗️ Arquitectura general

El repositorio contiene dos microservicios principales:

1. `ms-pedidos360-orders`
   - Encargado de gestionar pedidos, líneas de compra y el ciclo de vida de las transacciones comerciales.
   - Diseñado con enfoque transaccional y modelado orientado al dominio.
   - Está preparado para trabajar con PostgreSQL como base relacional, priorizando integridad referencial y consistencia.

2. `ms-pedidos360-catalog`
   - Encargado de administrar productos, inventario, precios y atributos del catálogo.
   - Diseñado para manejar estructuras más flexibles y consultas de lectura intensiva.
   - Está preparado para trabajar con MongoDB como base documental.

Cada microservicio se desarrolla como una aplicación Spring Boot independiente, con su propio `pom.xml`, configuración de arranque y pruebas unitarias.

---

## 📁 Estructura del repositorio

```text
pedidos360/
├── README.md
├── docker-compose.yml
├── ms-pedidos360-orders/
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       └── test/
├── ms-pedidos360-catalog/
│   ├── .gitattributes
│   ├── .gitignore
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       └── test/
└── .gitignore
```

---

## 🧩 Tecnologías principales

- Java 21
- Spring Boot 3 / 4 (según configuración del proyecto)
- Maven
- PostgreSQL para `orders`
- MongoDB para `catalog`
- Spring Data JPA
- Spring Data MongoDB
- Spring Web MVC
- Spring Validation
- Spring Security + OAuth2 Resource Server
- Actuator
- Lombok
- Pruebas con starter de Spring Boot

---

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- JDK 21 o superior
- Maven
- Git
- Docker y Docker Compose (recomendado para levantar bases de datos y entornos locales)

---

## ▶️ Cómo ejecutar localmente

### 1) Clonar el repositorio

```bash
git clone https://github.com/BenjaLizama/pedidos360.git
cd pedidos360
```

### 2) Ejecutar el microservicio de órdenes

```bash
cd ms-pedidos360-orders
./mvnw clean install
./mvnw spring-boot:run
```

### 3) Ejecutar el microservicio de catálogo

```bash
cd ../ms-pedidos360-catalog
./mvnw clean install
./mvnw spring-boot:run
```

> Cada servicio es una aplicación Spring Boot independiente y puede ejecutarse por separado, según el flujo de trabajo deseado.

### 4) Levantar infraestructura auxiliar

Si deseas preparar bases de datos y servicios complementarios localmente, puedes usar Docker Compose desde la raíz del repositorio:

```bash
docker compose up -d
```

> El archivo `docker-compose.yml` se encuentra en la raíz del proyecto y puede ser ampliado según la infraestructura necesaria para cada entorno.

---

## 🧪 Pruebas

Cada microservicio incluye estructura de pruebas bajo `src/test`.

Para ejecutar pruebas de un servicio:

```bash
cd ms-pedidos360-orders
./mvnw test
```

```bash
cd ../ms-pedidos360-catalog
./mvnw test
```

---

## 🛡️ Principios de diseño

Este proyecto busca aplicar buenas prácticas de arquitectura orientada a microservicios:

- Separación de dominios por servicio
- Independencia operativa entre microservicios
- Modelo de negocio desacoplado por contexto
- Preparación para seguridad y autenticación en capa de infraestructura
- Arquitectura adaptada a entornos cloud y despliegues distribuidos
- Base para integración con API Gateway y VPC Link

---

## 📌 Estado del proyecto

El repositorio presenta una base sólida para una solución de comercio electrónico modular. Actualmente se encuentra estructurado como una plataforma de microservicios con servicios independientes para órdenes y catálogo, listos para continuar ampliando dominio, casos de uso, seguridad, despliegue y observabilidad.

---

## 📎 Enlaces relevantes

- Repositorio: https://github.com/BenjaLizama/pedidos360
- Microservicio de órdenes: `ms-pedidos360-orders`
- Microservicio de catálogo: `ms-pedidos360-catalog`
