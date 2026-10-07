-- Datos de DEMOSTRACIÓN para el módulo de ofertas (vuelo, hospedaje y restaurante).
-- No contiene usuarios: las cuentas se crean desde la pantalla de registro.
-- Ejecutar una sola vez, después de esquema.sql. Los precios están en pesos colombianos
-- y la duración de los vuelos en minutos.

USE agenda_asistente_viajes;

INSERT INTO vuelo (origen, destino, fecha_salida, fecha_llegada, precio, aerolinea, disponibilidad, duracion) VALUES
('Bogotá', 'Cartagena', '2026-12-10 07:15:00', '2026-12-10 08:50:00', 289000.00, 'Avianca', TRUE, 95),
('Bogotá', 'Cartagena', '2026-12-11 14:30:00', '2026-12-11 16:05:00', 241000.00, 'Viva Air', TRUE, 95),
('Bogotá', 'Cartagena', '2027-01-05 06:00:00', '2027-01-05 07:35:00', 335000.00, 'LATAM', TRUE, 95),
('Medellín', 'Cartagena', '2026-12-12 09:40:00', '2026-12-12 10:55:00', 262000.00, 'Wingo', TRUE, 75),
('Bogotá', 'Medellín', '2026-12-10 08:00:00', '2026-12-10 08:55:00', 179000.00, 'Avianca', TRUE, 55),
('Bogotá', 'Medellín', '2026-12-18 17:20:00', '2026-12-18 18:15:00', 158000.00, 'Wingo', TRUE, 55),
('Cali', 'Medellín', '2026-12-20 11:10:00', '2026-12-20 12:05:00', 171000.00, 'Satena', FALSE, 55),
('Bogotá', 'Santa Marta', '2026-12-15 10:25:00', '2026-12-15 12:00:00', 312000.00, 'Avianca', TRUE, 95),
('Medellín', 'Santa Marta', '2026-12-16 13:45:00', '2026-12-16 15:10:00', 298000.00, 'LATAM', TRUE, 85),
('Bogotá', 'Cali', '2026-12-22 06:30:00', '2026-12-22 07:40:00', 205000.00, 'Avianca', TRUE, 70),
('Medellín', 'Cali', '2026-12-23 15:00:00', '2026-12-23 15:55:00', 149000.00, 'Viva Air', TRUE, 55);

INSERT INTO hospedaje (nombre, ubicacion, precio_noche, disponibilidad, calificacion, descripcion) VALUES
('Hotel Casa del Mar', 'Cartagena', 320000.00, TRUE, 4.50, 'Hotel en el centro histórico con desayuno incluido.'),
('Hostal Muralla Viva', 'Cartagena', 95000.00, TRUE, 4.10, 'Hostal económico a pocas cuadras de la ciudad amurallada.'),
('Apartamento Bocagrande', 'Cartagena', 210000.00, TRUE, 4.30, 'Apartamento con cocina y vista al mar.'),
('Hotel Poblado Plaza', 'Medellín', 280000.00, TRUE, 4.60, 'Hotel en El Poblado cerca de zonas comerciales.'),
('Hostal Laureles', 'Medellín', 85000.00, TRUE, 4.00, 'Habitaciones privadas y compartidas en Laureles.'),
('Hotel Rodadero Sol', 'Santa Marta', 240000.00, TRUE, 4.20, 'Hotel frente a la playa del Rodadero.'),
('Hotel Sucre Centro', 'Cali', 175000.00, FALSE, 4.40, 'Hotel céntrico, sin disponibilidad por ahora.'),
('Hostal Granada', 'Cali', 78000.00, TRUE, 3.90, 'Hostal en el barrio Granada, cerca de restaurantes.');

INSERT INTO restaurante (nombre, ubicacion, tipo_comida, precio_promedio, disponibilidad, calificacion, descripcion) VALUES
('La Cevichería', 'Cartagena', 'Mariscos', 78000.00, TRUE, 4.60, 'Cevichería de cocina caribeña.'),
('Café del Mural', 'Cartagena', 'Café y postres', 32000.00, TRUE, 4.20, 'Cafetería en el centro histórico.'),
('Hacienda Paisa', 'Medellín', 'Cocina paisa', 56000.00, TRUE, 4.50, 'Bandeja paisa y platos tradicionales antioqueños.'),
('Mercado del Río', 'Medellín', 'Variada', 48000.00, TRUE, 4.30, 'Plazoleta gastronómica con varias cocinas.'),
('El Pescador Samario', 'Santa Marta', 'Mariscos', 65000.00, TRUE, 4.40, 'Pescado fresco frente al mar.'),
('Sabor Vallecaucano', 'Cali', 'Cocina del Pacífico', 52000.00, TRUE, 4.10, 'Sancocho de gallina y platos típicos del Valle.');
