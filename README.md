# 🚀 Pedidos360 - Order Management System

**Pedidos360** es una solución de comercio electrónico basada en una arquitectura de microservicios moderna y desacoplada en Spring Boot, diseñada con separación estricta de dominios, control de auditoría, borrado lógico, contratos RESTful y una estrategia Cloud-native lista para integrarse con AWS API Gateway y VPC Link.

---

## 🏛️ Arquitectura del Sistema
El sistema se compone de dos microservicios independientes que utilizan bases de datos políglotas optimizadas para la naturaleza de sus datos:

1. **`orders` (Microservicio de Órdenes):**
    - Gestión transaccional de pedidos e ítems de compra.
    - Manejo de estados del ciclo de vida del pedido mediante `Enum` de alta eficiencia.
    - Implementación de control de auditoría base (`AuditableEntity`).
    - Inmutabilidad de registros transaccionales (sin borrado lógico).
    - **Base de datos:** PostgreSQL (Relacional) para garantizar integridad referencial, control de versiones y transacciones ACID estrictas.

2. **`catalog` (Microservicio de Catálogo e Inventario):**
    - Administración flexible de productos, precios, stock y atributos variables.
    - Herencia de auditoría y soporte para **borrado lógico** (`SoftDeleteEntity`) para descontinuar productos sin perder el historial.
    - Endpoints internos para validación y descuento automático de inventario.
    - **Base de datos:** MongoDB (NoSQL) para permitir esquemas flexibles de documentos y consultas de lectura masiva de alto rendimiento.

---

## 📊 Diagrama de Arquitectura
*(Próximamente: Espacio para insertar el diagrama general de la solución con API Gateway, VPC Link y los microservicios)*

---

## ⚙️ Requisitos Previos
Para ejecutar, compilar o desarrollar este proyecto de forma local, asegúrate de contar con lo siguiente instalado en tu entorno:

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Maven:** Para la gestión de dependencias y construcción del proyecto.
- **Docker y Docker Compose:** (Recomendado) Para levantar de forma rápida las instancias locales de PostgreSQL y MongoDB.
- **Git:** Para el control de versiones.

---

## 🚀 Instalación y Ejecución Local
*(Próximamente: Instrucciones paso a paso para clonar el repositorio, configurar las variables de entorno y levantar los servicios con Docker Compose)*

---

## 🛠️ Tecnologías y Características Principales
- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Data MongoDB.
- **Bases de Datos:** PostgreSQL (`orders`) y MongoDB (`catalog`).
- **APIs:** Contratos RESTful limpios con versionado (`/api/v1/...`).
- **Seguridad & Cloud-Native:** Preparado para integrarse con **OAuth2 / Resource Server**, **AWS API Gateway** y **VPC Link** para una comunicación interna privada y segura dentro de una VPC.