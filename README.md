# Knowy – Plataforma de Aprendizaje con Repetición Espaciada

Knowy es una aplicación web centrada en el aprendizaje eficaz y sostenible mediante la técnica de repetición
espaciada, inspirada en herramientas como Anki. Knowy tiene como objetivo consolidar el conocimiento de forma más
efectiva y productiva, enfocándose en reforzar aquellos contenidos que resultan más difíciles para cada usuario, y
permitiendo además valorar el grado de dificultad de las actividades propuestas.

## Índice de contenidos del proyecto Knowy

1. [Sobre el proyecto](#sobre-el-proyecto)
	- [Objetivo del proyecto](#objetivo-del-proyecto)
	- [Por qué Knowy](#por-qué-knowy)
	- [Tecnologías y herramientas utilizadas](#-tecnologías-y-herramientas-utilizadas)
	- [Requisitos](#-requisitos)
	- [Arquitectura del proyecto](#arquitectura-del-proyecto)
2. [Primeros pasos](#-primeros-pasos)
	- [Requisitos previos](#requisitos-previos)
	- [Instalación y ejecución Docker](#-instalación-y-ejecución-docker)
	- [Solución rápida de problemas comunes](#-solución-rápida-de-problemas-comunes)
3. [Licencia](#licencia)
4. [Colaboradores](#-colaboradores)

## Sobre el proyecto

### Objetivo del proyecto

Knowy está especialmente diseñado para apoyar el aprendizaje de programación en diferentes áreas, un campo en constante
crecimiento que, sin embargo, aún carece de herramientas específicas centradas en metodologías científicas de estudio
como la repetición espaciada. Nuestra plataforma busca cerrar esa brecha, facilitando el crecimiento profesional y
técnico de sus usuarios.

### Por qué Knowy

El nombre Knowy nace del término anglosajón “Know” (conocer, saber), alineado con el objetivo principal del proyecto:
ampliar el conocimiento y fomentar el crecimiento profesional. Con un enfoque actual y accesible, Knowy combina el
conocimiento con un toque divertido y cercano.

## 🛠️ Tecnologías y herramientas utilizadas

Knowy está construido utilizando una arquitectura moderna dividida en backend, frontend y herramientas de infraestructura:

### 🔧 Backend

- **Java 21** – Lenguaje principal.
- **Spring Boot 3.4.5** – Framework para construir la lógica de negocio.
- **Spring Security** – Seguridad de la aplicación.
- **Spring Data JPA** – Acceso a datos mediante ORM.
- **Spring Mail** – Envío de correos electrónicos.
- **Hibernate** – Implementación de JPA (incluida implícitamente).
- **Thymeleaf** – Motor de plantillas del lado del servidor.
- **Thymeleaf Layout Dialect** – Extensión para diseño de plantillas reutilizables.
- **Thymeleaf Extras Spring Security 6** – Integración de seguridad en plantillas.
- **JJWT (JSON Web Token)** – Autenticación basada en tokens (JWT).
- **Lombok** – Reducción de código boilerplate mediante anotaciones.
- **Spring Boot Actuator** – Monitorización y métricas del sistema.

### 📦 Construcción y gestión de dependencias

- **Maven** – Sistema de construcción y gestión de dependencias.
- **Maven Compiler Plugin** – Configuración del compilador y procesamiento de anotaciones (Lombok).
- **Spring Boot Maven Plugin** – Empaquetado de la aplicación.
- **Sass CLI Maven Plugin** – Compilación de estilos SCSS (Sass) a CSS.
- **Jacoco Maven Plugin** – Generación de reportes de cobertura de pruebas.

### 💾 Bases de datos y almacenamiento

- **PostgreSQL** – Base de datos relacional utilizada en tiempo de ejecución.

### 🖥️ Frontend

- **Thymeleaf** – Motor de plantillas HTML del lado del servidor.
- **Bootstrap** – Framework CSS para diseño responsivo.
- **SCSS (Sass)** – Preprocesador CSS para estilos personalizados.
- **JavaScript** – Para funcionalidades dinámicas e interactivas en la web.

### 🧪 Pruebas

- **Spring Boot Test** – Framework de pruebas para Spring.
- **Spring Security Test** – Pruebas de seguridad.
- **Mockito** – Framework de mocking para pruebas unitarias.
- **TestContainer** - Librería para ejecutar contenedores Docker efímeros en pruebas de integración.

### 🐳 Contenedores y despliegue

- **Docker** – Contenerización de la aplicación.
- **Docker Compose** – Orquestación de servicios backend/frontend/db.
- **Mailpit** – Servidor SMTP para desarrollo y pruebas de envío de correos.
- **Sonar** – Análisis estático de código para asegurar la calidad y mantener buenas prácticas.
- **Certbot (Let's Encrypt)** – Generación y renovación automática de certificados SSL para asegurar las comunicaciones HTTPS.
- **Spring Security + SSL** – Integración de los certificados SSL en la configuración del backend.

## ✅ Requisitos

- **Java 21** (JDK 21 o superior)  
- **Maven 3.9+**  
- **Docker**  
- **Docker Compose**

## Arquitectura del proyecto
### 🧩 Monorepo multimodular basado en DDD

El backend de **Knowy** se organiza bajo un **monorepositorio multimodular** con una arquitectura inspirada en los principios de **Domain-Driven Design (DDD)**.  
Esto significa que la estructura del código se modela en torno al **dominio del negocio**, y cada módulo representa un **bounded context** que encapsula su propia lógica, datos y reglas.

Cada módulo actúa como una unidad funcional independiente dentro del dominio, manteniendo un **alto nivel de cohesión interna** y **bajo acoplamiento** con el resto del sistema.  
El monorepo, por su parte, permite gestionar de forma unificada todos los contextos, garantizando una integración coherente y controlada entre ellos.

#### 🔄 Beneficios principales

- **Modelado por dominio:** el código se organiza siguiendo los límites naturales del negocio (bounded contexts).  
- **Escalabilidad y mantenibilidad:** cada módulo evoluciona de manera independiente sin afectar a otros contextos.  
- **Reutilización y coherencia:** se comparten librerías y configuraciones comunes entre módulos.  
- **Gestión unificada:** compilación, pruebas e integración continua centralizadas en un único repositorio.  

#### 🗂️ Estructura general del monorepo
```txt
knowy
├── builds
│   ├── knowy-devenv-compose     → Configuración de entorno de desarrollo (Docker Compose)
│   └── knowy-thymeleaf-compose  → Entorno de construcción para interfaz Thymeleaf
│
├── logs                         → Archivos de salida, registros y trazas del sistema
│
├── modules                      → Núcleo modular del sistema (organizado por dominios)
│   ├── application              → Capa de aplicación (casos de uso y orquestación)
│   │   ├── base                 → Funcionalidades y componentes compartidos
│   │   ├── course               → Contexto del dominio de cursos
│   │   ├── loader               → Contexto de carga de datos y recursos
│   │   └── user                 → Contexto del dominio de usuarios
│   │
│   └── infrastructure           → Capa técnica (adaptadores e implementaciones de infraestructura)
│       ├── backend-api-spring       → API principal del backend implementada con Spring Boot
│       ├── frontend-web-spring      → Interfaz web basada en Spring MVC y Thymeleaf
│       ├── notification-spring-mail → Notificaciones por correo (Spring Mail)
│       ├── persistence-spring-jpa   → Persistencia y acceso a datos (JPA/Hibernate)
│       ├── security-spring-jwt      → Seguridad y autenticación basada en JWT
│       ├── xml-data-loader          → Carga de datos desde archivos XML
│       └── xml-knowy-schemes        → Esquemas XML específicos del dominio Knowy
│
└── scripts
    └── init-postgresql
        └── sql                     → Scripts SQL de inicialización y configuración de PostgreSQL
```
### 🧩 Arquitectura Hexagonal dentro de los módulos (Ports & Adapters)

Cada carpeta dentro de `modules/application` (por ejemplo, `course`, `user`, `loader`, etc.) representa un **bounded context** del dominio, siguiendo los principios de **Domain-Driven Design (DDD)**.  

Dentro de cada módulo, la organización interna adopta el **patrón de Arquitectura Hexagonal (Ports & Adapters)**, lo que garantiza una **separación clara entre el dominio, la lógica de aplicación y las dependencias externas**.  

Este enfoque permite que la lógica de negocio sea completamente independiente de frameworks, infraestructura o de otros módulos del sistema, fomentando un diseño limpio y altamente mantenible.

#### 📁 Ejemplo de estructura de un módulo (`course`)
```txt
course
└── src.main.java
	└── com.knowy.core
		├── domain        → Modelos del dominio, entidades y lógica central del negocio
		├── exception     → Excepciones específicas del módulo
		├── port          → Interfaces (puertos) de entrada y salida
		└── usecase       → Casos de uso que implementan la lógica de aplicación
			├── adjust
			├── exercise
			├── importer
			├── ...
```
#### 🔄 Beneficios de la arquitectura hexagonal
- **Aislamiento:** cada módulo mantiene su lógica independiente del framework, evitando dependencias directas entre módulos o con la infraestructura.  
- **Testabilidad:** los casos de uso pueden probarse sin necesidad de componentes externos.  
- **Extensibilidad:** permite incorporar o reemplazar implementaciones (por ejemplo, bases de datos o servicios) sin afectar la lógica central.  
- **Mantenibilidad:** cada módulo evoluciona de forma autónoma, minimizando el impacto en el resto del sistema.  

# 🏁 Primeros pasos

### Requisitos previos

Antes de comenzar, asegúrate de tener instaladas las siguientes herramientas en tu entorno de desarrollo:

- [Java 21 JDK](https://adoptium.net/en-GB/temurin/releases/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

Puedes verificar las versiones instaladas ejecutando:

```bash
java -version
mvn -v
docker -v
docker compose version
```

### 🚀 Instalación y ejecución Docker

#### 1. Clona el repositorio

```bash
git clone https://github.com/Knowy-Learn/knowy.git
cd knowy
```

#### 2. Levanta los servicios con Docker

El proyecto incluye un script automatizado (knowy) que facilita el arranque y la gestión de todos los servicios necesarios (backend, base de datos, correo y frontend).
Asegúrate de que Docker esté corriendo antes de continuar.

**🧭 Mostrar ayuda del script**
Desde la raíz del proyecto, ejecuta:
```bash
   ./knowy help
```
Esto mostrará los diferentes modos de arranque disponibles y sus opciones.

**▶️ Iniciar el proyecto en modo local**
Para iniciar todos los servicios en modo local (seguro y con valores por defecto generados automáticamente):
```bash
   ./knowy up thymeleaf local
```
> ⚠️ **Nota:** Este modo está pensado solo para entornos de desarrollo o pruebas locales. No se recomienda usarlo en producción, ya que emplea configuraciones y credenciales generadas automáticamente. Para entornos externos, configura las variables de entorno indicadas en el repositorio de documentación del proyecto.

**Verificar contenedores activos**

Puedes comprobar que los servicios están en ejecución con:
```bash
docker compose ps
```

Esto iniciará:

- PostgreSQL
- Mailpit
- La aplicación Java Knowy

#### 3. Accede a la aplicación

Abre tu navegador y visita: http://localhost
Si la aplicación está corriendo correctamente, deberías ver la interfaz principal de Knowy.

También puedes acceder a http://localhost:8025
para utilizar la interfaz del cliente SMTP (Mailpit), útil para gestionar y visualizar correos enviados.

#### 4. Cerrar o Limpiar la aplicación

Para detener los servicios:
```bash
   ./knowy down thymeleaf local #<- cierra la aplicación
```

Para detener y limpiar todos los archivos generados (volúmenes, imágenes, etc.):
```bash
   ./knowy clean thymeleaf  	#<- Cierrar y limpia los archivos generados de la aplicación
```

### 🛠️ Solución rápida de problemas comunes

- **Docker Compose no encontrado:** Asegúrate de que Docker Compose esté instalado y en tu PATH. En versiones recientes
  de Docker Desktop viene integrado.
- **Puertos ocupados:** Detén la aplicación que esté usando el puerto.
- **Errores en compilación Maven:** Confirma que tu JDK está en versión 21 y que Maven es 3.8 o superior. También revisa
  que tengas conexión a internet para descargar dependencias.
- **No se conecta a la base de datos:** Verifica que el contenedor PostgreSQL esté corriendo y que las credenciales
  coincidan con las configuradas en el backend.

## Licencia

Este proyecto está licenciado bajo la GNU General Public License v3.0 (GPLv3).

Esto significa que:

- Puedes usar, modificar y distribuir el software libremente.
- Cualquier modificación o trabajo derivado debe publicarse bajo la misma licencia GPLv3.
- Debes proporcionar el código fuente de cualquier versión modificada que distribuyas.
- El software se proporciona “tal cual”, sin garantía de ningún tipo.

Para más información, puedes leer el texto completo de la licencia en el archivo [LICENSE](LICENSE) o consultar el sitio
oficial:<br>
👉 https://www.gnu.org/licenses/gpl-3.0.html

## 🤝 Colaboradores

Este proyecto ha sido posible gracias al trabajo conjunto de todas las personas que han contribuido en diferentes etapas de su desarrollo.  
A cada una, ¡gracias por su esfuerzo, tiempo y dedicación! 💪✨

---

### 👩‍💻 Dirección técnica y desarrollo principal
- [**SaraMForte**](https://github.com/SaraMForte)  
  Lideró la organización técnica y la arquitectura base del proyecto, además de desarrollar la mayor parte del código y mantener la coherencia a lo largo de todas las versiones.

---

### 🏗️ Arquitectura de software
- [**jagame90**](https://github.com/jagame)  
  Responsable de plantear la **arquitectura general** y las bases estructurales del proyecto, contribuyendo a su visión técnica global.

---

### 💻 Desarrollo backend y frontend (hasta v1.0)
- [**Aarón Helices Martín-Niño**](https://github.com/aaronhmn)  
- [**David Gil Campos**](https://github.com/Sinnick29)  
- [**NRichardsF**](https://github.com/nrichardsf)  

Colaboraron activamente en el desarrollo de funcionalidades clave, tanto en el **frontend** como en el **backend**, aportando una base sólida durante las primeras versiones del proyecto.

---

### 🧩 Colaboraciones puntuales (hasta v1.0)
- [**Iván Carvajal Huetor**](https://github.com/ivancarvajalhuetor)  
- [**Judit Olaya Grima Lorente**](https://github.com/JuditoLaya)  
- [**Manuel Corvo Belda**](https://github.com/Worldlover12)  
- [**Jorgazo**](https://github.com/JorgeSR95)  

Realizaron contribuciones en áreas específicas del desarrollo y ayudaron a dar forma a distintas partes del proyecto en sus primeras fases.

---

💫 **Gracias a todas las personas que han formado parte de este proyecto, en cualquier etapa o versión.**  
Cada aportación ha sido fundamental para llegar hasta aquí. 🚀
