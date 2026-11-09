-- Base de datos para la API HealthBlood (Gluapi)
CREATE DATABASE IF NOT EXISTS gluco_api
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE gluco_api;

CREATE TABLE IF NOT EXISTS usuario (
    idUsuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    primerApellido VARCHAR(40) NOT NULL,
    segundoApellido VARCHAR(40),
    email VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS medida (
    idMedida INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL,
    glucosa INT NOT NULL,
    fechaHora DATETIME,
    contexto VARCHAR(40),
    notas VARCHAR(100),
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
);

-- Datos de ejemplo: 17 usuarios con IDs unicos 1-17 y 49 medidas.
-- INSERT IGNORE permite volver a ejecutar el script sin duplicar filas.
INSERT IGNORE INTO usuario
    (idUsuario, nombre, primerApellido, segundoApellido, email)
VALUES
    (1, 'Sebastian', 'G', 'R', 'sebastian@gmail.com'),
    (2, 'María', 'López', 'García', 'maria@test.com'),
    (3, 'Lucía', 'Fernández', 'Ramírez', 'lucia.fernandez@gluapi.com'),
    (4, 'Carlos', 'Mendoza', 'Torres', 'carlos.mendoza@gluapi.com'),
    (5, 'Ana', 'Morales', 'Castro', 'ana.morales@gluapi.com'),
    (6, 'Diego', 'Hernández', 'Vega', 'diego.hernandez@gluapi.com'),
    (7, 'Valentina', 'Ortiz', 'Salazar', 'valentina.ortiz@gluapi.com'),
    (8, 'Sofía', 'Aguilar', 'Navarro', 'sofia.aguilar@gluapi.com'),
    (9, 'Mateo', 'Cruz', 'Ríos', 'mateo.cruz@gluapi.com'),
    (10, 'Isabella', 'Romero', 'Peña', 'isabella.romero@gluapi.com'),
    (11, 'Daniel', 'Castillo', 'Lara', 'daniel.castillo@gluapi.com'),
    (12, 'Camila', 'Vargas', 'Solís', 'camila.vargas@gluapi.com'),
    (13, 'Andrés', 'Navarro', 'Fuentes', 'andres.navarro@gluapi.com'),
    (14, 'Paula', 'Jiménez', 'Mora', 'paula.jimenez@gluapi.com'),
    (15, 'Ricardo', 'Herrera', 'Luna', 'ricardo.herrera@gluapi.com'),
    (16, 'Elena', 'Castro', 'Duarte', 'elena.castro@gluapi.com'),
    (17, 'Javier', 'Rojas', 'Medina', 'javier.rojas@gluapi.com');

INSERT IGNORE INTO medida
    (idMedida, idUsuario, glucosa, fechaHora, contexto, notas)
VALUES
    (1, 1, 95, '2026-11-06 08:00:00', 'Ayuno', 'Medicion matutina'),
    (2, 1, 140, '2026-11-06 14:00:00', 'Despues de comer', 'Comida con arroz'),
    (3, 1, 110, '2026-11-06 20:00:00', 'Antes de dormir', NULL),
    (4, 1, 123, '2026-11-08 14:30:00', 'Prueba', 'MedidaPruebaEndpoints'),
    (5, 3, 102, '2026-11-02 07:30:00', 'Ayuno', 'Glucosa estable'),
    (6, 3, 148, '2026-11-02 14:15:00', 'Después de comer', 'Comida con pasta'),
    (7, 3, 118, '2026-11-02 22:00:00', 'Antes de dormir', NULL),
    (8, 4, 130, '2026-11-03 08:00:00', 'Ayuno', 'Estrés en el trabajo'),
    (9, 4, 165, '2026-11-03 15:00:00', 'Después de comer', 'Comida rápida'),
    (10, 4, 125, '2026-11-04 07:45:00', 'Ayuno', 'Caminata matutina'),
    (11, 5, 90, '2026-11-04 08:30:00', 'Ayuno', 'Buen control'),
    (12, 5, 132, '2026-11-04 13:30:00', 'Después de comer', 'Ensalada y pollo'),
    (13, 5, 105, '2026-11-05 21:30:00', 'Antes de dormir', 'Sin merienda'),
    (14, 6, 175, '2026-11-05 09:00:00', 'Ayuno', 'Glucosa alta'),
    (15, 6, 155, '2026-11-05 14:00:00', 'Después de comer', 'Arroz y frijoles'),
    (16, 6, 140, '2026-11-06 07:00:00', 'Ayuno', 'Mejorando'),
    (17, 7, 88, '2026-11-06 08:15:00', 'Ayuno', 'Ejercicio temprano'),
    (18, 7, 112, '2026-11-06 13:45:00', 'Después de comer', 'Comida ligera'),
    (19, 7, 96, '2026-11-07 22:15:00', 'Antes de dormir', 'Buen descanso'),
    (20, 8, 95, '2026-11-07 07:00:00', 'Ayuno', 'Buen inicio del día'),
    (21, 8, 138, '2026-11-07 13:30:00', 'Después de comer', 'Comida con tortillas'),
    (22, 8, 112, '2026-11-07 21:30:00', 'Antes de dormir', NULL),
    (23, 9, 145, '2026-11-07 08:00:00', 'Ayuno', 'Glucemia alta'),
    (24, 9, 160, '2026-11-07 14:30:00', 'Después de comer', 'Comida con refresco'),
    (25, 9, 120, '2026-11-08 07:30:00', 'Ayuno', 'Mejor control'),
    (26, 10, 98, '2026-11-07 08:30:00', 'Ayuno', 'Ejercicio antes del desayuno'),
    (27, 10, 122, '2026-11-07 15:00:00', 'Después de comer', 'Comida ligera'),
    (28, 10, 104, '2026-11-08 22:00:00', 'Antes de dormir', 'Cena temprano'),
    (29, 11, 135, '2026-11-08 07:15:00', 'Ayuno', 'Poco descanso'),
    (30, 11, 158, '2026-11-08 14:00:00', 'Después de comer', 'Comida con arroz'),
    (31, 11, 128, '2026-11-08 21:45:00', 'Antes de dormir', 'Caminata nocturna'),
    (32, 12, 87, '2026-11-08 08:00:00', 'Ayuno', 'Glucosa baja'),
    (33, 12, 118, '2026-11-08 13:00:00', 'Después de comer', 'Desayuno tardío'),
    (34, 12, 101, '2026-11-09 07:30:00', 'Ayuno', 'Buen control'),
    (35, 13, 150, '2026-11-08 09:00:00', 'Ayuno', 'Estrés'),
    (36, 13, 170, '2026-11-08 15:30:00', 'Después de comer', 'Comida abundante'),
    (37, 13, 142, '2026-11-09 08:00:00', 'Ayuno', 'Mejorando'),
    (38, 14, 92, '2026-11-08 07:45:00', 'Ayuno', 'Rutina estable'),
    (39, 14, 126, '2026-11-08 14:15:00', 'Después de comer', 'Ensalada con atún'),
    (40, 14, 108, '2026-11-09 21:30:00', 'Antes de dormir', 'Sin merienda'),
    (41, 15, 160, '2026-11-08 08:15:00', 'Ayuno', 'Glucosa alta'),
    (42, 15, 180, '2026-11-08 14:45:00', 'Después de comer', 'Comida con postre'),
    (43, 15, 150, '2026-11-09 07:00:00', 'Ayuno', 'Ajuste de medicación'),
    (44, 16, 85, '2026-11-08 07:30:00', 'Ayuno', 'Glucosa baja'),
    (45, 16, 110, '2026-11-08 13:15:00', 'Después de comer', 'Comida equilibrada'),
    (46, 16, 97, '2026-11-09 22:00:00', 'Antes de dormir', 'Buen descanso'),
    (47, 17, 128, '2026-11-09 08:00:00', 'Ayuno', 'Inicio del día'),
    (48, 17, 152, '2026-11-09 14:30:00', 'Después de comer', 'Comida con pasta'),
    (49, 17, 124, '2026-11-09 21:00:00', 'Antes de dormir', 'Cena ligera');
