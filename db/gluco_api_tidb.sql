-- Base de datos para TiDB Cloud Starter (MySQL-compatible).
-- TiDB no implementa utf8mb4_0900_ai_ci; se usa utf8mb4_general_ci.
CREATE DATABASE IF NOT EXISTS gluco_api
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE gluco_api;

CREATE TABLE IF NOT EXISTS usuario (
    idUsuario INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    primerApellido VARCHAR(40) NOT NULL,
    segundoApellido VARCHAR(40),
    email VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS medida (
    idMedida INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL,
    glucosa INT NOT NULL,
    fechaHora DATETIME,
    contexto VARCHAR(40),
    notas VARCHAR(100),
    CONSTRAINT fk_medida_usuario
        FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Datos actuales del proyecto: 2 usuarios y 4 medidas.
-- INSERT IGNORE permite volver a ejecutar el script sin duplicar filas.
INSERT IGNORE INTO usuario
    (idUsuario, nombre, primerApellido, segundoApellido, email)
VALUES
    (1, 'Sebastian', 'G', 'R', 'sebastian@gmail.com'),
    (10, 'María', 'López', 'García', 'maria@test.com');

INSERT IGNORE INTO medida
    (idMedida, idUsuario, glucosa, fechaHora, contexto, notas)
VALUES
    (1, 1, 95, '2026-11-06 08:00:00', 'Ayuno', 'Medicion matutina'),
    (2, 1, 140, '2026-11-06 14:00:00', 'Despues de comer', 'Comida con arroz'),
    (3, 1, 110, '2026-11-06 20:00:00', 'Antes de dormir', NULL),
    (4, 1, 123, '2026-11-08 14:30:00', 'Prueba', 'MedidaPruebaEndpoints');
