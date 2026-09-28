USE agenda_asistente_viajes;

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
