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
