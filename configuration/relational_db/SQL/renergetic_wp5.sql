/* USERS CONFIGURATION */
INSERT INTO uuid VALUES ('2');
INSERT INTO users ( id, keycloak_id, uuid ) VALUES
( 10, '81ef41c5-2c18-483c-8b2b-7b7c66c17835', '2' );

INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid, asset_category_id ) VALUES
( 10, 'ren-admin', 'RENergetic Administratos', 18, 10, '2', NULL );

/* PILOT CONFIGURATION */
INSERT INTO asset(
    id, name, geo_location, label, asset_category_id, parent_asset_id, asset_type_id, user_id, uuid)
    VALUES (1, 'psnc', NULL, 'PSNC', NULL, NULL, 3, NULL, '2'),
    (2, 'datacenter', NULL, 'PSNC data center', NULL, 1, 23, NULL, '2'),
    (3, 'office', NULL, 'PSNC office', NULL, 1, 20, NULL, '2'),
    (4, 'hvac', NULL, 'PSNC HVAC systems', NULL, 3, 21, NULL, '2'),
    (5, 'office_without_hvac', NULL, 'PSNC office without HVAC systems', NULL, 3, 20, NULL, '2'),
    (6, 'altair', NULL, 'Altair', NULL, 2, 22, NULL, '2'),
    (7, 'psnc_garden', NULL, 'PSNC garden', NULL, 1, 27, NULL, '2');

INSERT INTO measurement(
    id, name, sensor_name, domain, direction, measurement_type_id, asset_id, sensor_id, label,
    asset_category_id, energy_island_asset_id, uuid)
    VALUES (2000, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 4, NULL, NULL, NULL, NULL, '2'),
    (2001, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 4, NULL, NULL, NULL, NULL, '2'),
    (2002, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 5, NULL, NULL, NULL, NULL, '2'),
    (2003, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 5, NULL, NULL, NULL, NULL, '2'),
    (2004, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 3, NULL, NULL, NULL, NULL, '2'),
    (2005, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 3, NULL, NULL, NULL, NULL, '2'),
    (2006, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 2, NULL, NULL, NULL, NULL, '2'),
    (2007, 'energy_consumed', 'energy_meter', 'electricity', 'in', 8, 2, NULL, NULL, NULL, NULL, '2'),
    (2008, 'current_power', 'energy_meter', 'electricity', 'in', 6, 4, NULL, NULL, NULL, NULL, '2'),
    (2009, 'current_power', 'energy_meter', 'electricity', 'in', 6, 4, NULL, NULL, NULL, NULL, '2'),
    (2010, 'current_power', 'energy_meter', 'electricity', 'in', 6, 5, NULL, NULL, NULL, NULL, '2'),
    (2011, 'current_power', 'energy_meter', 'electricity', 'in', 6, 5, NULL, NULL, NULL, NULL, '2'),
    (2012, 'current_power', 'energy_meter', 'electricity', 'in', 6, 3, NULL, NULL, NULL, NULL, '2'),
    (2013, 'current_power', 'energy_meter', 'electricity', 'in', 6, 3, NULL, NULL, NULL, NULL, '2'),
    (2014, 'current_power', 'energy_meter', 'electricity', 'in', 6, 2, NULL, NULL, NULL, NULL, '2'),
    (2015, 'current_power', 'energy_meter', 'electricity', 'in', 6, 2, NULL, NULL, NULL, NULL, '2'),
    (2016, 'current_power', 'energy_meter', 'electricity', 'in', 6, 6, NULL, NULL, NULL, NULL, '2'),
    (2017, 'current_power', 'energy_meter', 'electricity', 'in', 6, 6, NULL, NULL, NULL, NULL, '2'),
    (2018, 'current_power', 'energy_meter', 'electricity', 'in', 6, 1, NULL, NULL, NULL, NULL, '2'),
    (2019, 'current_power', 'energy_meter', 'electricity', 'in', 6, 1, NULL, NULL, NULL, NULL, '2'),
    (2020, 'power', 'pv', 'electricity', 'out', 1, 3, 'Inv1', NULL, NULL, NULL, '2'),
    (2021, 'power', 'pv', 'electricity', 'out', 1, 3, 'Inv2', NULL, NULL, NULL, '2'),
    (2022, 'power', 'pv', 'electricity', 'out', 1, 3, 'Inv4', NULL, NULL, NULL, '2'),
    (2023, 'power', 'pv', 'electricity', 'out', 1, 3, 'Inv5', NULL, NULL, NULL, '2'),
    (2024, 'power', 'pv', 'electricity', 'out', 1, 3, 'all_inverters', NULL, NULL, NULL, '2'),
    (2025, 'temperature.2m', 'weather', NULL, NULL, 4, 7, NULL, NULL, NULL, NULL, '2'),
    (2026, 'heat_used', 'heat_meter', 'heat', 'in', 17, 3, NULL, NULL, NULL, NULL, '2'),
    (2027, 'heat_generated', 'heat_meter', 'heat', 'out', 17, 2, NULL, NULL, NULL, NULL, '2'),
    (2028, 'relative_to_weekly_average', 'report', 'electricity', 'in', 5, 1, NULL, NULL, NULL, NULL, '2'),
    (2029, 'relative_to_monthly_average', 'report', 'electricity', 'in', 5, 1, NULL, NULL, NULL, NULL, '2'),
    (2030, 'co2_reduction', 'report', NULL, NULL, 18, 1, NULL, NULL, NULL, NULL, '2'),
    (2031, 'energy_produced', 'pv', 'electricity', 'out', 8, 3, 'all_inverters', NULL, NULL, NULL, '2'),
    (2032, 'heat_lost', 'heat_meter', 'heat', 'out', 17, 2, NULL, NULL, NULL, NULL, '2');
    
INSERT INTO tags(
    id, key, value)
    VALUES (1000, 'type_data', 'real'),
    (1001, 'interpolation_method', 'none'),
    (1002, 'interpolation_method', 'spline'),
    (1003, 'interpolation_method', 'spline_and_value_repetition');
    
INSERT INTO measurement_tags(
    tag_id, measurement_id)
    VALUES (1000, 2000),
    (1000, 2001),
    (1000, 2002),
    (1000, 2003),
    (1000, 2004),
    (1000, 2005),
    (1000, 2006),
    (1000, 2007),
    (1000, 2008),
    (1000, 2009),
    (1000, 2010),
    (1000, 2011),
    (1000, 2012),
    (1000, 2013),
    (1000, 2014),
    (1000, 2015),
    (1000, 2016),
    (1000, 2017),
    (1000, 2018),
    (1000, 2019),
    (1000, 2020),
    (1000, 2021),
    (1000, 2022),
    (1000, 2023),
    (1000, 2024),
    (1000, 2025),
    (1000, 2026),
    (1000, 2027),
    (1000, 2028),
    (1000, 2029),
    (1000, 2030),
    (1000, 2031),
    (1000, 2032),
    (1001, 2000),
    (1002, 2001),
    (1001, 2002),
    (1002, 2003),
    (1001, 2004),
    (1002, 2005),
    (1001, 2006),
    (1002, 2007),
    (1001, 2008),
    (1003, 2009),
    (1001, 2010),
    (1003, 2011),
    (1001, 2012),
    (1003, 2013),
    (1001, 2014),
    (1003, 2015),
    (1001, 2016),
    (1002, 2017),
    (1001, 2018),
    (1002, 2019),
    (1001, 2020),
    (1001, 2021),
    (1001, 2022),
    (1001, 2023),
    (1001, 2024),
    (1001, 2025),
    (1001, 2026),
    (1001, 2027),
    (1001, 2031),
    (1001, 2032);
