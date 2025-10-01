
-----------------------------------------------------------------------
-- SECCIÓN 1: INSERCIÓN DE MARCAS DE VEHÍCULOS
-----------------------------------------------------------------------

-- Marcas más vendidas y populares en Argentina
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Fiat', '/imgs/logos/fiat.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Toyota', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Volkswagen', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Renault', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Chevrolet', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Peugeot', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Ford', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Nissan', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Citroën', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Jeep', '/imgs/logos/car.png');
-- Marcas Premium y de Lujo
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Mercedes-Benz', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('BMW', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Audi', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Lexus', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Volvo', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Porsche', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Land Rover', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Jaguar', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Mini', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('DS Automobiles', '/imgs/logos/car.png');
-- Marcas Asiáticas (Japonesas, Coreanas, etc.)
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Honda', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Hyundai', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Kia', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Suzuki', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Subaru', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Mitsubishi', '/imgs/logos/car.png');
-- Marcas Chinas (con presencia creciente)
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Chery', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Haval', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('JAC', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Geely', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('BAIC', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Lifan', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('DFM', '/imgs/logos/car.png'); -- Dongfeng
-- Marcas de Utilitarios y Camiones
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('RAM', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Iveco', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Scania', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Foton', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Agrale', '/imgs/logos/car.png');
-- Otras marcas Europeas y Americanas
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Dodge', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Chrysler', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Alfa Romeo', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Maserati', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Ferrari', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Seat', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Cupra', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('SsangYong', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('KTM', '/imgs/logos/car.png'); -- Por el X-Bow, aunque es más de motos.
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('Shineray', '/imgs/logos/car.png');
INSERT INTO marcas_vehiculos (nombre_marca_vehiculo, ruta_logo) VALUES ('BYD', '/imgs/logos/car.png'); -- Marca de eléctricos

-----------------------------------------------------------------------
-- SECCIÓN 2: INSERCIÓN DE MODELOS DE VEHÍCULOS
-----------------------------------------------------------------------

-- Modelos Fiat (fk_marca = 1)
-- Modelos iniciales con motorización 1.8 (2018-2022)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.8', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Precision 1.8', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos HGT 1.8', '2019', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Precision 1.8', '2020', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos S-Design 1.8', '2021', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.8', '2022', 1.8, 1);
-- Modelos con motorización 1.3 (desde 2018)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3', '2018', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3', '2019', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3', '2020', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Like 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos S-Design 1.3', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Like 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Precision 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3 CVT', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Precision 1.3 CVT', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Like 1.3 MT', '2025', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Drive 1.3 MT Pack Plus', '2025', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Cronos Precision 1.3 CVT', '2025', 1.3, 1);
-- Modelos con motorización 1.3
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2018', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2019', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Trekking 1.3', '2019', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2020', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Trekking 1.3', '2020', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Trekking 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3 CVT', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Drive 1.3', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Trekking 1.3', '2024', 1.3, 1);
-- Modelos con motorización 1.8 (descontinuada en modelos recientes)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Precision 1.8', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo HGT 1.8', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Precision 1.8 AT', '2019', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo HGT 1.8', '2019', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Argo Precision 1.8', '2020', 1.8, 1);
-- Año 2016
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2016', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2016', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2016', 2.0, 1);
-- Año 2017
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2017', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2017', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2017', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Blackjack 2.0 Turbodiésel', '2017', 2.0, 1);
-- Año 2018
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2018', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2018', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Blackjack 2.0 Turbodiésel', '2018', 2.0, 1);
-- Año 2019
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2019', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2019', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2019', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ranch 2.0 Turbodiésel', '2019', 2.0, 1);
-- Año 2020
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2020', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2020', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ranch 2.0 Turbodiésel', '2020', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2020', 2.0, 1);
-- Año 2021 (año de la gran actualización)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2021', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 1.3 Turbo Nafta', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2021', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2021', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2021', 2.0, 1);
-- Año 2022
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.8 Nafta', '2022', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 1.3 Turbo Nafta', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2022', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2022', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2022', 2.0, 1);
-- Año 2023
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.3 Turbo Nafta', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 1.3 Turbo Nafta', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2023', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2023', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2023', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ranch 2.0 Turbodiésel', '2023', 2.0, 1);
-- Año 2024
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.3 Turbo Nafta', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 1.3 Turbo Nafta', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2024', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2024', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2024', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ranch 2.0 Turbodiésel', '2024', 2.0, 1);
-- Año 2025
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 1.3 Turbo Nafta', '2025', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 1.3 Turbo Nafta', '2025', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Freedom 2.0 Turbodiésel', '2025', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Volcano 2.0 Turbodiésel', '2025', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ultra 2.0 Turbodiésel', '2025', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Toro Ranch 2.0 Turbodiésel', '2025', 2.0, 1);
-- Primeros años y versiones (1998-2004)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '1998', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.6', '1999', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.3', '2000', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.6', '2001', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2002', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.3', '2003', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.8', '2004', 1.8, 1);
-- Años intermedios y consolidación de versiones (2005-2008)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.4', '2006', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.8', '2006', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.8', '2007', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.8', '2008', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2008', 1.4, 1);
-- Final del período y nuevos motores/versiones (2009-2010)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.8', '2009', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.3 Multijet', '2010', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure Locker 1.8', '2010', 1.8, 1);
-- Versiones de la generación anterior (2011-2019)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.6', '2012', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.3 Multijet', '2013', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.6 Locker', '2014', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.6 3 Puertas', '2016', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Trekking 1.4', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Working 1.4', '2018', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Adventure 1.6 Cabina Doble', '2019', 1.6, 1);
-- Modelos de la nueva generación (2020-hoy)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Endurance 1.4', '2020', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Freedom 1.3', '2020', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Volcano 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Ranch 1.3 CVT', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Endurance 1.4', '2022', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Freedom 1.3', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Volcano 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Ultra 1.0 Turbo CVT', '2024', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Ranch 1.0 Turbo CVT', '2024', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Freedom 1.3', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Strada Endurance 1.4', '2024', 1.4, 1);
-- Año 2016
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Easy', '2016', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way', '2016', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2016', 1.0, 1);
-- Año 2017
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Easy', '2017', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way', '2017', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2017', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way On', '2017', 1.0, 1);
-- Año 2018
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Easy', '2018', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way', '2018', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2018', 1.0, 1);
-- Año 2019
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Easy', '2019', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way', '2019', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2019', 1.0, 1);
-- Año 2020
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Easy', '2020', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Way', '2020', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2020', 1.0, 1);
-- Año 2021
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2021', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Trekking', '2021', 1.0, 1);
-- Año 2022
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2022', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Trekking', '2022', 1.0, 1);
-- Año 2023
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2023', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Trekking', '2023', 1.0, 1);
-- Año 2024
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2024', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Trekking', '2024', 1.0, 1);
-- Año 2025
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Like', '2025', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Mobi Trekking', '2025', 1.0, 1);
-- Año 2022 (Año de lanzamiento y consolidación en el mercado)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3 CVT', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Audace 1.0 Turbo', '2022', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2022', 1.0, 1);
-- Año 2023
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3 CVT', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Audace 1.0 Turbo', '2023', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2023', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2023', 1.3, 1);
-- Año 2024
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3 CVT', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Audace 1.0 Turbo', '2024', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2024', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2024', 1.3, 1);
-- Año 2025
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2025', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Audace 1.0 Turbo', '2025', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2025', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2025', 1.3, 1);
-- Versión inicial (479 cc)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Nuova', '1957', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Economica', '1958', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Normale', '1958', 0.5, 1);
-- Versión D (499 cc)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 D', '1960', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 D', '1961', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 D', '1962', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 D', '1963', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 D', '1964', 0.5, 1);
-- Versión F (con puertas de apertura frontal)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1965', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1966', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1967', 0.5, 1);
-- Versión L (Lusso)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1968', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 L', '1968', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1969', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 L', '1969', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1970', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 L', '1970', 0.5, 1);
-- Versiones F y L (499 cc)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1971', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 L', '1971', 0.5, 1);
-- Versión R (594 cc)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 F', '1972', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 L', '1972', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 R', '1972', 0.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 R', '1973', 0.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 R', '1974', 0.6, 1);
-- Último año de producción
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 R', '1975', 0.6, 1);
-- Versión Giardiniera (499 cc)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Giardiniera', '1971', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Giardiniera', '1972', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Giardiniera', '1973', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Giardiniera', '1974', 0.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Giardiniera', '1975', 0.5, 1);
-- Primeros años (2007-2010)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.2', '2007', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.2', '2008', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.4', '2010', 1.4, 1);
-- Introducción del motor TwinAir y Abarth
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 TwinAir', '2011', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Sport 1.4', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 TwinAir', '2012', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.4', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2012', 1.4, 1);
-- Años intermedios (2013-2015)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.2', '2013', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 TwinAir', '2014', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2015', 1.4, 1);
-- Después del facelift (2016-2017)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.2', '2016', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.2', '2016', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2016', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 0.9 TwinAir', '2017', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2017', 1.4, 1);
-- Últimos años de la generación a combustión
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.2', '2018', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 0.9 TwinAir', '2018', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2018', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 1.2', '2019', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Pop 1.2', '2020', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Lounge 0.9 TwinAir', '2020', 0.9, 1);
-- Convivencia de modelos a combustión y eléctricos
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Eléctrico', '2020', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e', '2021', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2021', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e Icon', '2022', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e La Prima', '2022', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500 Abarth 1.4 T-Jet', '2022', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e', '2023', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e Abarth', '2023', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e', '2024', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e Abarth', '2024', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e', '2025', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('500e Abarth', '2025', 0, 1);
-- Primera Generación (basada en Fiat 127)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 0.9', '1977', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 0.9', '1978', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.05', '1979', 1.05, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Diesel', '1980', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.05', '1981', 1.05, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Diesel', '1982', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.05', '1983', 1.05, 1);
-- Segunda Generación (basada en Fiat Uno)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.0', '1984', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3', '1985', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Diesel', '1986', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1987', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1987', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1988', 1.4, 1);
-- Últimos años de la primera fase de la segunda generación
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1989', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1989', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1990', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1990', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.0', '1991', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1991', 1.7, 1);
-- Modelos de la segunda fase (post-facelift de 1992)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1992', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1992', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.5', '1993', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1994', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.6', '1995', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1996', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1997', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '1998', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4', '1999', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '2000', 1.7, 1);
-- Últimos años de la segunda generación (basada en Fiat Uno)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3', '2001', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '2002', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3', '2003', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.7 Diesel', '2004', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3', '2005', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3', '2006', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2007', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2008', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2009', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2010', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2011', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2012', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.3 Fire', '2013', 1.3, 1);
-- Lanzamiento de la nueva generación (basada en Novo Uno)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2014', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2015', 1.4, 1);
-- Versiones de la tercera generación antes del facelift
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2016', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2018', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2020', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Fire Evo', '2021', 1.4, 1);
-- Modelos después del facelift de 2022
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Evo', '2022', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Evo', '2023', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Evo', '2024', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Fiorino 1.4 Evo', '2025', 1.4, 1);
-- Modelos después del rediseño de 1989
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno S 1.0', '1989', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno SX 1.1', '1989', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Turbo 1.4', '1989', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno S 1.0', '1990', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno SX 1.4', '1990', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Turbo 1.4', '1990', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno S 1.1', '1991', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno SX 1.4', '1991', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno 1.3 Diesel', '1991', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno S 1.1', '1992', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno SX 1.4', '1992', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Turbo 1.4', '1992', 1.4, 1);
-- Últimos años de la primera generación (Uno Mille / Uno Fire)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Mille 1.0', '1993', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Mille 1.0', '1996', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Fire 1.0', '2001', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Fire 1.0', '2005', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Fire 1.0', '2008', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Mille Economy', '2010', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Uno Mille Way', '2013', 1.0, 1);
-- Modelos de la nueva generación (Novo Uno)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Vivace 1.0 Evo', '2010', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Way 1.4 Evo', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Sporting 1.4 Evo', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Vivace 1.0 Evo', '2013', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Way 1.4 Evo', '2014', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Attractive 1.4 Evo', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Way 1.3 Firefly', '2016', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Sporting 1.3 Firefly', '2017', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Attractive 1.3 Firefly', '2018', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Way 1.3 Firefly', '2019', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Attractive 1.3 Firefly', '2020', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Uno Ciao 1.3', '2021', 1.3, 1);
-- Primera generación (post-facelift 1998)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio 1.3', '1998', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio EL 1.6', '1999', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio 1.7 Diesel', '2000', 1.7, 1);
-- Segunda generación (facelift 2001)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Fire 1.0', '2001', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio ELX 1.3', '2002', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio HLX 1.8', '2003', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Fire 1.0', '2004', 1.0, 1);
-- Tercera generación (facelift 2005)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Fire 1.3', '2005', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio ELX 1.4', '2006', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio 1.8 R', '2007', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Fire 1.4', '2008', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Attractive 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Attractive 1.4', '2010', 1.4, 1);
-- Cuarta generación (Novo Palio 2011)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Attractive 1.4', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Attractive 1.0', '2012', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Essence 1.6', '2013', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Fire', '2014', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Attractive 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Attractive 1.4', '2016', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Novo Palio Attractive 1.4', '2017', 1.4, 1);
-- Primera generación (1996-2000)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena 1.4', '1996', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena HL 1.6', '1997', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena 1.7 Diesel', '1998', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena EL 1.4', '1999', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena HLX 1.6', '2000', 1.6, 1);
-- Segunda generación (2001-2004)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena Fire 1.3', '2001', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena ELX 1.4', '2002', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena HLX 1.8', '2003', 1.8, 1);
-- Tercera generación (2005-2012)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena Fire 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena EL 1.4', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena ELX 1.8', '2009', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Siena EL 1.4', '2011', 1.4, 1);
-- Cuarta generación (Grand Siena) y últimas versiones
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Attractive 1.4', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Essence 1.6', '2013', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Attractive 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Essence 1.6', '2017', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Attractive 1.4', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grand Siena Attractive 1.4', '2021', 1.4, 1);
-- Versiones de la primera generación
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna 1.3', '1988', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SD 1.7', '1989', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna S 1.5', '1990', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SD 1.7', '1991', 1.7, 1);
-- Modelos de la segunda generación (post-facelift de 1992)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SCV 1.6', '1992', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SCX 1.4', '1993', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna Weekend 1.7 Diesel', '1994', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SCV 1.6', '1995', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna Weekend 1.7 Diesel', '1996', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SD 1.7', '1997', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SCV 1.6', '1998', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna SD 1.7', '1999', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Duna Weekend 1.7 Diesel', '2000', 1.7, 1);
-- Primeros años de producción en Argentina
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 C 1.05', '1981', 1.05, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 CL 1.3', '1982', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 CL5 1.3', '1983', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 CL5 1.3 Diesel', '1984', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 TR 1.3', '1985', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('147 TR5 1.3 Diesel', '1986', 1.3, 1);
-- Modelos renombrados a Spazio y luego a Uno
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio TR 1.3', '1987', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio TR Diesel', '1988', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio TR 1.4', '1989', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio Vivace 1.3', '1990', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio Vivace 1.4', '1991', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio TR', '1992', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio CL', '1993', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio CL', '1994', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio CL', '1995', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Spazio CL', '1996', 1.4, 1);
-- Primera Generación (1993-1999)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 55 S 1.1', '1993', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 60 S 1.2', '1994', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto GT 1.4 Turbo', '1995', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 75 S 1.2', '1996', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto GT 1.4 Turbo', '1997', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.7 TD', '1998', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 60 S 1.2', '1999', 1.2, 1);
-- Segunda Generación (1999-2005)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto ELX 1.2', '1999', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto HGT 1.8', '2000', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto JTD 1.9 Diesel', '2001', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto HLX 1.8', '2002', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto ELX 1.2', '2003', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.8 Sporting', '2004', 1.8, 1);
-- Tercera Generación (Grande Punto / Punto Evo / Punto)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grande Punto 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grande Punto 1.3 Multijet', '2006', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Grande Punto 1.4 T-Jet', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto Essence 1.6', '2008', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto Evo 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto Evo 1.3 Multijet', '2010', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto Sporting 1.6', '2011', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.4', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 0.9 TwinAir', '2013', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto Essence 1.6', '2014', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.3 Multijet', '2016', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.4', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Punto 1.2', '2018', 1.2, 1);
-- Primeros años de producción en Argentina
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 1.1', '1971', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 1.3', '1972', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Familiar', '1974', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 IAVA 1.3', '1975', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 IAVA 1.3', '1976', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 L', '1977', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Familiar', '1978', 1.3, 1);
-- Modelos Europa y Super Europa
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Europa 1.3', '1979', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Europa 1.3 Diesel', '1980', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.3', '1982', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.5', '1984', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.3 Diesel', '1986', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.5', '1988', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.3 Diesel', '1989', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('128 Super Europa 1.5', '1990', 1.5, 1);
-- Versiones iniciales (2002-2006)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.2', '2002', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.6 16v', '2003', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2004', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.3 Multijet', '2005', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2006', 1.4, 1);
-- Versiones después del facelift (2007-2012)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.3 Multijet', '2008', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2010', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Albea 1.4', '2012', 1.4, 1);
-- Versiones de la primera y única generación
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.4 S', '1995', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.6 SX', '1996', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.9 TD', '1997', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.8 HGT', '1998', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.6 SX', '1999', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.9 JTD', '2000', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Brava 1.6 SX', '2001', 1.6, 1);
-- Primera Generación (1995-2001)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4 S', '1995', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 SX', '1996', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo HGT 2.0', '1997', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.9 TD', '1998', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 SX', '1999', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.9 JTD', '2000', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 SX', '2001', 1.6, 1);
-- Sin producción bajo el nombre "Bravo" desde 2002 hasta 2006 (reemplazado por el Fiat Stilo)
-- Segunda Generación (2007-2016)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4 T-Jet', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 Multijet', '2008', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4 T-Jet', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4 Multiair', '2010', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo Sport 1.4 T-Jet', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 Multijet', '2012', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4 Multiair', '2013', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.6 Multijet', '2014', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Bravo 1.8 E.torQ', '2016', 1.8, 1);
-- Primera Generación (1985-1996)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0', '1985', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.5 V6', '1986', 2.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 i.e. Turbo', '1987', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 CHT', '1988', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.5 TD', '1989', 2.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 i.e.', '1990', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 Turbo', '1991', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 1.6', '1992', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.5 V6', '1993', 2.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 TD', '1994', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.5 V6', '1995', 2.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.0 i.e.', '1996', 2.0, 1);
-- Sin producción entre 1997 y 2004
-- Segunda Generación (2005-2010)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 1.8', '2005', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 1.9 Multijet', '2006', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.2', '2007', 2.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 1.9 Multijet', '2008', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 2.4 Multijet', '2009', 2.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Croma 1.9 Multijet', '2010', 1.9, 1);
-- Primera Generación (2000-2010)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.2', '2000', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 1.6', '2001', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.9 JTD', '2002', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.3 Multijet', '2006', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 1.9 Multijet', '2008', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.4', '2009', 1.4, 1);
-- Segunda Generación (2010-2022)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Panorama 1.4', '2010', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.6 Multijet', '2011', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.4', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 2.0 Multijet', '2013', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.3 Multijet', '2015', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.4 T-Jet', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Eléctrico', '2018', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Cargo 1.4 T-Jet', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Eléctrico', '2021', 0, 1);
-- Tercera Generación (a partir de 2022)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 1.5 Diesel', '2022', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Eléctrico', '2023', 0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò 1.5 Diesel', '2024', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Doblò Eléctrico', '2025', 0, 1);
-- Versiones Europeas
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea 1.3 Multijet', '2006', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea 1.6 Multijet', '2009', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea 1.4', '2012', 1.4, 1);
-- Versiones Latinoamericanas (basadas en la plataforma del Palio)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Attractive 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea HLX 1.8', '2006', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Adventure 1.8', '2007', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea ELX 1.4', '2008', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Essence 1.6 E.torQ', '2010', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Sporting 1.8 E.torQ', '2011', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Attractive 1.4', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Essence 1.6 E.torQ', '2013', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Adventure 1.8 E.torQ', '2014', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Essence 1.6', '2015', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Idea Attractive 1.4', '2016', 1.4, 1);
-- Versiones de la única generación del modelo
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Absolute 1.8 E.torQ', '2009', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea 1.4 T-Jet', '2010', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Essence 1.8 E.torQ', '2011', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea 1.4 T-Jet', '2012', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Essence 1.8 E.torQ', '2013', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Blackmotion 1.8 E.torQ', '2014', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Essence 1.8 E.torQ', '2015', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Linea Essence 1.8 E.torQ', '2016', 1.8, 1);
-- Primera Generación (1980-2003)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 30 0.65', '1980', 0.65, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 45 0.9', '1982', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 0.9', '1983', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 750', '1986', 0.75, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.0 Fire', '1987', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 1.1', '1995', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.3 Diesel', '2001', 1.3, 1);
-- Segunda Generación (2003-2011)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.1', '2003', 1.1, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.2', '2004', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 1.2', '2005', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.3 Multijet', '2006', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 100 HP', '2007', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 1.3 Multijet', '2008', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.2', '2010', 1.2, 1);
-- Tercera Generación (2012-hoy)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 0.9 TwinAir', '2012', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.2', '2013', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 0.9 TwinAir', '2014', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.3 Multijet', '2015', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.2', '2017', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 0.9 TwinAir', '2019', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.0 Hybrid', '2020', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.0 Hybrid', '2022', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 4x4 0.9 TwinAir', '2024', 0.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Panda 1.0 Hybrid', '2025', 1.0, 1);
-- Primera Generación (desde 2021)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2021', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2021', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Audace 1.0 Turbo', '2022', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2022', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2023', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2023', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Drive 1.3', '2024', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Impetus 1.0 Turbo', '2025', 1.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Pulse Abarth 1.3 Turbo', '2025', 1.3, 1);
-- Versiones del Fiat Qubo
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.4', '2011', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.3 Multijet', '2012', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.4', '2013', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.3 Multijet', '2014', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.3 Multijet', '2016', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.4', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.3 Multijet', '2018', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.4', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Qubo 1.3 Multijet', '2020', 1.3, 1);
-- Versiones Europeas
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 85', '1983', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 100', '1984', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 1.9 DS', '1985', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 2.0 i.e.', '1986', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 1.7 D', '1987', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 1.6', '1988', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 100', '1989', 1.6, 1);
-- Versiones producidas en Argentina, incluyendo los últimos años del modelo
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 85', '1990', 1.5, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta 2000', '1991', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta Weekend 1.6', '1992', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta Weekend 1.9 DS', '1993', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta SC', '1994', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Regatta Weekend', '1995', 1.6, 1);
-- Versiones Europeas
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.2 16v', '2001', 1.2, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.6 16v', '2002', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Abarth 2.4', '2003', 2.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.9 JTD', '2004', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Multiwagon 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.9 Multijet', '2006', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Multiwagon 1.6', '2007', 1.6, 1);
-- Versiones producidas en Brasil
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.8', '2002', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 2.4 Abarth', '2003', 2.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Schumacher', '2005', 2.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo 1.8', '2007', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Blackmotion 1.8', '2008', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Sporting 1.8', '2009', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Stilo Attractive 1.8', '2010', 1.8, 1);
-- Versiones Europeas
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 1.4', '1990', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 1.6 i.e.', '1991', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 1.9 TD', '1992', 1.9, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 2.0 i.e. 16V', '1993', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra SW 1.8', '1994', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 1.6 i.e.', '1995', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 1.9 TD', '1996', 1.9, 1);
-- Versiones producidas en Brasil
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 2.0', '1992', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra 2.0 Turbo', '1993', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra SW', '1994', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra Ouro 2.0', '1995', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra Stile', '1996', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra City', '1997', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra Turbo', '1998', 2.0, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Tempra SX', '1999', 2.0, 1);
-- Primera Generación (1997-2000)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend 1.6', '1997', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend TD', '1998', 1.7, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend 1.6', '1999', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend TD', '2000', 1.7, 1);
-- Segunda Generación (2001-2004)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend ELX 1.3', '2001', 1.3, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend 1.8', '2002', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Palio Weekend 1.4', '2004', 1.4, 1);
-- Tercera Generación (2005-2008)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend ELX 1.4', '2005', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend 1.8', '2006', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Adventure 1.8', '2007', 1.8, 1);
-- Cuarta y última Generación (2009-2020)
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Trekking 1.4', '2009', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Adventure 1.8', '2010', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Essence 1.6 E.torQ', '2011', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Trekking 1.6', '2012', 1.6, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Adventure 1.8', '2014', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Trekking 1.4', '2015', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Adventure 1.8', '2016', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Attractive 1.4', '2017', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Adventure 1.8', '2018', 1.8, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Attractive 1.4', '2019', 1.4, 1);
INSERT INTO modelos_vehiculos (nombre_modelo, anio, cilindrada, fk_marca) VALUES ('Weekend Attractive 1.4', '2020', 1.4, 1);
