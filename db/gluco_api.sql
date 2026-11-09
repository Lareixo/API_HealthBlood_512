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

INSERT INTO usuario (nombre, primerApellido, segundoApellido, email)
SELECT 'Sebastian', 'G', 'R', 'sebastian@gmail.com'
WHERE NOT EXISTS (SELECT 1 FROM usuario);

-- Medidas de ejemplo para el primer usuario (solo si la tabla esta vacia)
INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas)
SELECT u.idUsuario, s.glucosa, s.fechaHora, s.contexto, s.notas
FROM (
    SELECT 95 AS glucosa, '2026-11-06 08:00:00' AS fechaHora, 'Ayuno' AS contexto, 'Medicion matutina' AS notas
    UNION ALL
    SELECT 140, '2026-11-06 14:00:00', 'Despues de comer', 'Comida con arroz'
    UNION ALL
    SELECT 110, '2026-11-06 20:00:00', 'Antes de dormir', NULL
) AS s
JOIN (SELECT idUsuario FROM usuario ORDER BY idUsuario LIMIT 1) AS u
WHERE NOT EXISTS (SELECT 1 FROM medida);

-- Usuario de ejemplo que ya existe en TiDB (mismo ID).
INSERT IGNORE INTO usuario
    (idUsuario, nombre, primerApellido, segundoApellido, email)
VALUES
    (10, 'María', 'López', 'García', 'maria@test.com');

-- ---------------------------------------------------------------------------
-- Datos ficticios adicionales para que los endpoints tengan variedad.
-- Usuarios 11-15, con 3 medidas cada uno (IDs 5-19).
-- INSERT IGNORE permite ejecutar el script varias veces sin duplicar filas.
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO usuario
    (idUsuario, nombre, primerApellido, segundoApellido, email)
VALUES
    (11, 'Lucía', 'Fernández', 'Ramírez', 'lucia.fernandez@gluapi.com'),
    (12, 'Carlos', 'Mendoza', 'Torres', 'carlos.mendoza@gluapi.com'),
    (13, 'Ana', 'Morales', 'Castro', 'ana.morales@gluapi.com'),
    (14, 'Diego', 'Hernández', 'Vega', 'diego.hernandez@gluapi.com'),
    (15, 'Valentina', 'Ortiz', 'Salazar', 'valentina.ortiz@gluapi.com');

-- Medida base adicional (ID 4) y medidas ficticias (IDs 5-19).
INSERT IGNORE INTO medida
    (idMedida, idUsuario, glucosa, fechaHora, contexto, notas)
VALUES
    (4, 1, 123, '2026-11-08 14:30:00', 'Prueba', 'MedidaPruebaEndpoints'),
    (5, 11, 102, '2026-11-02 07:30:00', 'Ayuno', 'Glucosa estable'),
    (6, 11, 148, '2026-11-02 14:15:00', 'Después de comer', 'Comida con pasta'),
    (7, 11, 118, '2026-11-02 22:00:00', 'Antes de dormir', NULL),
    (8, 12, 130, '2026-11-03 08:00:00', 'Ayuno', 'Estrés en el trabajo'),
    (9, 12, 165, '2026-11-03 15:00:00', 'Después de comer', 'Comida rápida'),
    (10, 12, 125, '2026-11-04 07:45:00', 'Ayuno', 'Caminata matutina'),
    (11, 13, 90, '2026-11-04 08:30:00', 'Ayuno', 'Buen control'),
    (12, 13, 132, '2026-11-04 13:30:00', 'Después de comer', 'Ensalada y pollo'),
    (13, 13, 105, '2026-11-05 21:30:00', 'Antes de dormir', 'Sin merienda'),
    (14, 14, 175, '2026-11-05 09:00:00', 'Ayuno', 'Glucosa alta'),
    (15, 14, 155, '2026-11-05 14:00:00', 'Después de comer', 'Arroz y frijoles'),
    (16, 14, 140, '2026-11-06 07:00:00', 'Ayuno', 'Mejorando'),
    (17, 15, 88, '2026-11-06 08:15:00', 'Ayuno', 'Ejercicio temprano'),
    (18, 15, 112, '2026-11-06 13:45:00', 'Después de comer', 'Comida ligera'),
    (19, 15, 96, '2026-11-07 22:15:00', 'Antes de dormir', 'Buen descanso');
