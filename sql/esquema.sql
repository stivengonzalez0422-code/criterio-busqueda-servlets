-- Esquema de la base de datos del proyecto "Agenda con tu asistente virtual".
-- Las definiciones salen del SQL original del proyecto (agenda_asistente_viajes.sql).
-- Todas las sentencias usan IF NOT EXISTS: se puede ejecutar sobre una base que ya tenga datos.

CREATE DATABASE IF NOT EXISTS agenda_asistente_viajes
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agenda_asistente_viajes;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    `contraseña` VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS criterio_busqueda (
    id_criterio INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    destino VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    precio_maximo DECIMAL(12,2) NOT NULL,
    horario VARCHAR(100),
    preferencias TEXT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_criterio_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT chk_precio_criterio
        CHECK (precio_maximo >= 0),
    CONSTRAINT chk_fechas_criterio
        CHECK (fecha_fin >= fecha_inicio)
);

CREATE TABLE IF NOT EXISTS vuelo (
    id_vuelo INT AUTO_INCREMENT PRIMARY KEY,
    origen VARCHAR(150) NOT NULL,
    destino VARCHAR(150) NOT NULL,
    fecha_salida DATETIME NOT NULL,
    fecha_llegada DATETIME NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    aerolinea VARCHAR(150) NOT NULL,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    duracion INT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_precio_vuelo CHECK (precio >= 0),
    CONSTRAINT chk_duracion_vuelo CHECK (duracion > 0),
    CONSTRAINT chk_fechas_vuelo CHECK (fecha_llegada > fecha_salida)
);

CREATE TABLE IF NOT EXISTS hospedaje (
    id_hospedaje INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    precio_noche DECIMAL(12,2) NOT NULL,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    calificacion DECIMAL(3,2),
    descripcion TEXT,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_precio_hospedaje CHECK (precio_noche >= 0),
    CONSTRAINT chk_calificacion_hospedaje CHECK (calificacion BETWEEN 0 AND 5)
);

CREATE TABLE IF NOT EXISTS restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    tipo_comida VARCHAR(150) NOT NULL,
    precio_promedio DECIMAL(12,2) NOT NULL,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    calificacion DECIMAL(3,2),
    descripcion TEXT,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_precio_restaurante CHECK (precio_promedio >= 0),
    CONSTRAINT chk_calificacion_restaurante CHECK (calificacion BETWEEN 0 AND 5)
);
