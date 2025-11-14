INSERT INTO public.news (title, content, publish_date) VALUES
('Knowy v1.2.0 lanzado: nuevo módulo de carga de datos y mejoras generales',
'La versión **v1.2.0** de Knowy introduce el nuevo módulo loader-core, que permite cargar cursos desde XML mediante la clase DataLoader. Se han añadido pruebas unitarias para CourseImporter y JpaCourseRepository, se crearon esquemas XSD en el módulo xml-knowy-schemes y se refactorizó la estructura del proyecto para modularizar los componentes (loader-core, frontend-web-spring, jpa-persistence, angular-front-server). Se corrigieron errores relacionados con el filtrado de categorías en el frontend, el acceso temprano a la base de datos, y mejoras para soporte en Linux. Finalmente, la documentación (Javadocs) se ha actualizado en varios módulos. ([github.com](https://github.com/Knowy-Learn/knowy/releases))',
'2025-10-27'),

('Knowy v1.0.0: versión estable inicial con mejoras de usabilidad',
'Con la versión v1.0.0 se lanzó una edición estable de Knowy que incorpora mejoras significativas: opciones de filtrado y ordenación en la página "My Courses", barra de progreso vinculada al número de cursos adquiridos, corrección del zoom en la pantalla de curso y lecciones, además de mejoras en la lógica de recomendación de cursos para priorizar el idioma. También se añadió JavaDoc al proyecto, mejorando la calidad de la documentación. ([github.com](https://github.com/Knowy-Learn/knowy/releases))',
'2025-07-23'),

('Knowy v0.9.0 (pre-release): Seguridad con JWT, perfil de usuario completo y arquitectura modular',
'En la versión v0.9.0 (pre-release) se incorporó una integración completa de seguridad con Spring Security y autenticación basada en JWT. El proyecto añadió configuración multi-entorno con archivos .env y soporte para Mailpit para recuperación de contraseña. También introdujo nuevos servicios en el backend (UserService, CourseSubscriptionService, etc.), entidades de dominio (Curso, Lección, Idioma, Progreso de usuario), lógica de suscripción y recomendación de cursos, y validación del nickname con filtro de palabras prohibidas. En el frontend se añadieron nuevas vistas y se eliminó lógica legada, preparándolo para producción. ([github.com](https://github.com/Knowy-Learn/knowy/releases))',
'2025-07-10'),

('Knowy v0.8.0 (pre-release): Preparando la infraestructura Docker y la pila inicial',
'La versión v0.8.0 (pre-release) marcó la configuración de la infraestructura y la base del proyecto: se añadieron varios archivos docker-compose (como compose.yaml, compose-dev.yaml, etc.) para levantar el stack completo (servidor, base de datos, mailpit) en desarrollo y producción. En el backend se incluyeron nuevas dependencias como Spring Data JPA, PostgreSQL, Spring Mail, JWT, Lombok; y nuevos controladores (CoursesController, LessonController, QuizController, UserConfigController, etc.). En el frontend se realizó una revisión completa de la interfaz con Thymeleaf + SCSS, nuevos assets, validaciones en formularios, y estructura modular de componentes. ([github.com](https://github.com/Knowy-Learn/knowy/releases))',
'2025-07-02'),

('Knowy v0.3.0: Inicio del proyecto e interfaz básica',
'La versión v0.3.0 (pre-release) corresponde al arranque del proyecto: inicialización del backend con Spring Boot, integración de SCSS para estilos y completadas las pantallas básicas de la aplicación (landing page, login, registro). Esta versión estableció la base técnica para el posterior desarrollo de la plataforma. ([github.com](https://github.com/Knowy-Learn/knowy/releases))',
'2025-06-01');