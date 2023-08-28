
INSERT INTO uuid VALUES ('2');

/* CREATE ASSET CATEGORIES */
INSERT INTO asset_category ( id, name, label, description ) VALUES
( 1, 'office', 'office', NULL ),
( 2, 'datacenter', 'datacenter', NULL ),
( 3, 'supercomputer', 'supercomputer', NULL ),
( 4, 'hvac','hvac', NULL ),
( 5, 'heat_exchanger','heat_exchanger', NULL ),
( 6, 'residence', 'residence', NULL );

/* CREATE USERS */
INSERT INTO users ( id, keycloak_id, uuid ) VALUES
( 1, 'd8040f51-77e7-4dd8-9bc3-567ccd501adf', '2' ),
( 2, 'b7c25233-815e-4e26-ae3a-b18519df14ce', '2' );

INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid, asset_category_id ) VALUES
( 1, 'user1', 'User 1', 18, 1, '2', NULL ),
( 2, 'user2', 'User 2', 18, 2, '2', NULL );

/* CREATE ISLAND */
INSERT INTO asset ( id, name, label, asset_type_id, uuid, asset_category_id ) VALUES
( 3, 'energy_island', 'Energy Island', 4, '2', NULL );

/* CREATE BUILDINGS */
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid, asset_category_id ) VALUES
( 4, 'building2', 'Building 2', 3, 3, '2', 6 ),
( 5, 'flat1', 'Flat 1', 2, 4, '2', 6 ),
( 6, 'building1', 'Building 1', 3, 3, '2', 6 ),
( 7, 'datacenter1', 'Datacenter 1', 23, 3, '2', 2),
( 8, 'office', 'Office', 20, 3, '2', 1),
( 9, 'hvac', 'HVAC', 21, 3, '2', 4 ),
( 10, 'altair', 'Altair', 22, 4, '2', 3 ),
( 11, 'eagle', 'Eagle', 22,4, '2', 3 ),
( 12, 'psnc_garden', 'PSNC Garden', 3, 3, '2',null ),
( 13, 'heat_exchanger', 'Heat exchanger', 24, 3, '2',null );

/* CREATE ENERGY ASSETS */
INSERT INTO asset ( id, name, label, asset_type_id, uuid, asset_category_id ) VALUES
( 14, 'gas_boiler1', 'Gas Boiler 1', 7, '2', NULL ),
( 15, 'gas_boiler2', 'Gas Boiler 2', 7, '2', NULL ),
( 16, 'solar_collector1', 'Solar Collector 1', 13, '2', NULL ),
( 17, 'pv_panel_1', 'PV Panel 1', 10, '2', NULL ),
( 18, 'wind_farm_1', 'Wind Farm 1', 26, '2', NULL );

/* ASSET DETAILS */
INSERT INTO asset_details ( id, key, value, asset_id ) VALUES
( 1, 'threshold_heat_max', '4.75', 4 ),
( 2, 'threshold_heat_min', '0', 4 ),
( 3, 'threshold_electricity_max', '11.5', 4 ),
( 4, 'threshold_electricity_min', '0', 4 ),
( 5, 'threshold_heat_max', '4.7', 6 ),
( 6, 'threshold_heat_min', '0', 6 ),
( 7, 'threshold_electricity_max', '11.3', 6 ),
( 8, 'threshold_electricity_min', '0', 6 ),
( 9, 'threshold_heat_max', '10', 16 ),
( 10, 'threshold_heat_min', '0', 16 ),
( 11, 'threshold_electricity_max', '10', 17 ),
( 12, 'threshold_electricity_min', '0', 17 ),
( 13, 'threshold_electricity_max', '10', 18 ),
( 14, 'threshold_electricity_min', '0', 18 );

/* CONNECT ASSETS */
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type ) VALUES
( 1, 1, 3, 'owner' ),
( 2, 2, 3, 'owner' ),
( 3, 1, 5, 'owner' ),
( 4, 2, 5, 'resident' ),
( 5, 2, 6, 'owner' ),
( 6, 1, 7, 'owner' ),
( 7, 2, 7, 'owner' );
    
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type ) VALUES
( 14, 5, 7, 'energy_connection' ),
( 15, 6, 8, 'energy_connection' ),
( 16, 6, 9, 'energy_connection' );

/* CREATE MEASUREMENTS */
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) VALUES
( 21, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 5, '2' ),
( 22, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 6, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) VALUES
( 23, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'heat_meter', 5, '2' ),
( 24, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'heat_meter', 6, '2' ),
( 25, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'heat_meter', 4, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) VALUES
( 26, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'gas_boiler', 7, '2' ),
( 27, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'gas_boiler', 8, '2' ),
( 28, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'solar_collector', 9, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, sensor_id, asset_id, asset_category_id, uuid ) VALUES
( 29, 'heat_exhanged', 'Heat exhanged', 1, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL,'2' ),
( 30, 'heat_exhanged', 'Heat exhanged', 2, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL,'2' ),
( 31, 'temperature.2m', 'Temperature out', 4, NULL, NULL, 'weather', NULL, 12, NULL,'2' ),
( 32, 'current_power', 'Current power', 1, 'electricity', 'out', 'photovoltaics', 'inv1', 12, NULL, '2' ),
( 33, 'current_power', 'Current power', 1, 'electricity', 'out', 'photovoltaics', 'inv2', 12, NULL, '2' ),
( 34, 'current_power', 'Current power', 1, 'electricity', 'out', 'photovoltaics', 'inv3', 12, NULL, '2' ),
( 35, 'current_power', 'Current power', 1, 'electricity', 'out', 'photovoltaics', 'inv4', 12, NULL, '2' ),
( 36, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, 11,  NULL, '2' ),
( 37, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, 11, NULL, '2' ),
( 38, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, 10, NULL, '2' ),
( 39, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, 10, NULL, '2' ),
( 40, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, 9, NULL, '2' ),
( 41, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, 9, NULL, '2' ),
( 42, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, 8, NULL, '2' ),
( 43, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, 8, NULL, '2' ),
( 44, 'heat_exhanged', 'Heat exhanged', 5, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 45, 'heat_wasted', 'Heat wasted', 5, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 46, 'heat_used', 'Heat used', 5, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 47, 'heat_exhanged', 'Heat exhanged', 2, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 48, 'heat_wasted', 'Heat wasted', 2, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 49, 'heat_used', 'Heat used', 2, 'heat', 'out', 'heat_exhanger', NULL, 13, NULL, '2' ),
( 60, 'renewability', 'Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 61, 'renewability', 'Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 62, 'non_renewability', 'Non Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 63, 'non_renewability', 'Non Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 1 */
( 64, 'current_power', 'Electricity', 7, 'electricity', 'in', 'electricity_meter', NULL, NULL, 1, '2' ),
( 65, 'energy_consumed', 'Electricity', 9, 'electricity', 'in', 'electricity_meter', NULL, NULL, 1, '2' ),
( 66, 'current_power', 'Heat', 7, 'heat', 'in', 'heat_meter', NULL, NULL, 1, '2' ),
( 67, 'energy_consumed', 'Heat', 9, 'heat', 'in', 'heat_meter', NULL, NULL, 1, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 2 */
( 68, 'current_power', 'Electricity', 7, 'electricity', 'in', 'electricity_meter', NULL, NULL, 2, '2' ),
( 69, 'energy_consumed', 'Electricity', 9, 'electricity', 'in', 'electricity_meter', NULL, NULL, 2, '2' ),
( 70, 'current_power', 'Heat', 7, 'heat', 'in', 'heat_meter', NULL, NULL, 2, '2' ),
( 71, 'energy_consumed', 'Heat', 9, 'heat', 'in', 'heat_meter', NULL, NULL, 2, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 3 */		  
( 72, 'current_power', 'Electricity', 7, 'electricity', 'in', 'electricity_meter', NULL, NULL, 3, '2' ),
( 73, 'energy_consumed', 'Electricity', 9, 'electricity', 'in', 'electricity_meter', NULL, NULL, 3, '2' ),
( 74, 'current_power', 'Heat', 7, 'heat', 'in', 'heat_meter', NULL, NULL, 3, '2' ),
( 75, 'energy_consumed', 'Heat', 9, 'heat', 'in', 'heat_meter', NULL, NULL, 3, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 4 */
( 76, 'current_power', 'Electricity', 7, 'electricity', 'in', 'electricity_meter', NULL, NULL, 4, '2' ),
( 77, 'energy_consumed', 'Electricity', 9, 'electricity', 'in', 'electricity_meter', NULL, NULL, 4, '2' ),
( 78, 'current_power', ' Heat', 7, 'heat', 'in', 'heat_meter', NULL, NULL, 4, '2' ),
( 79, 'energy_consumed', 'Heat', 9, 'heat', 'in', 'heat_meter', NULL, NULL, 4, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 5 */
( 80, 'current_power', 'Current power', 7, 'heat', 'out', 'energy_meter', NULL, NULL, 5, '2' ),
( 81, 'energy_consumed', 'Current energy', 9, 'heat', 'out', 'energy_meter', NULL, NULL, 5, '2' ),
( 82, 'heat_exhanged', 'Heat exhanged', 7, 'heat', 'out', 'heat_exhanger', NULL, NULL, 5, '2' ),
( 83, 'heat_wasted', 'Heat wasted', 7, 'heat', 'out', 'heat_exhanger', NULL, NULL, 5, '2' ),
/* RENEWABILITY MEASUREMENTS FOR ENTIRE ISLAND */
( 84, 'renewable', 'Renewable', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL, '2' ),
( 85, 'non_renewable', 'Fosil Fuels', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL, '2' ),
( 86, 'external', 'External', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL, '2' ),
( 87, 'renewable', 'Renewable', 9, 'heat', 'out', 'heat_meter', NULL, 3, NULL, '2' ),
( 88, 'non_renewable', 'Fosil Fuels', 9, 'heat', 'out', 'heat_meter', NULL, 3, NULL, '2' ),
( 89, 'external', 'External', 9, 'heat', 'out', 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
( 90, 'renewable', 'Renewable', 9, NULL, 'out', 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
( 91, 'non_renewable', 'Fosil Fuels', 9, NULL, 'out', 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
( 92, 'external', 'External', 9, NULL, 'out', 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
( 100, 'total_produced', 'Produced energy', 5, NULL, NULL, 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
( 101, 'total_imported', 'Imported energy', 5, NULL, NULL, 'heat_meter,electricity_meter', NULL, 3, NULL, '2' ),
/* MEASUREMENTS FOR CURRENT RENEWABILITY SCREEN */
(110, 'renewability_heat', 'Heat renewability', 5, 'heat', null, 'renewability', null, 5, null, '2'),
(111, 'renewability_heat', 'Heat renewability', 5, 'heat', null, 'renewability', null, 6, null, '2');

INSERT INTO tags ( id, key, value ) VALUES
( 50, 'measurement_type', 'renewable' ),
( 51, 'measurement_type', 'non_renewable' ),
( 52, 'measurement_type', 'external' ),
( 53, 'measurement_type', 'renewable,non_renewable' );

INSERT INTO measurement_tags (measurement_id, tag_id) VALUES
(84, 50),
(85, 51),
(86, 52),
(87, 50),
(88, 51),
(89, 52),
(90, 50),
(91, 51),
(92, 52),
(100, 53),
(101, 52);

/* TILES AND PANELS */
INSERT INTO information_panel ( id, name, label, is_template, featured, uuid ) VALUES
( 1, 'consumption_screen', 'Energy Consumption', false, true, '2' ),
( 2, 'production_screen', 'Energy Production', false, true, '2' );

INSERT INTO information_tile ( id, name, label, layout, props, type, information_panel_id ) VALUES
/* PRODUCTION SCREEN */
( 1, 'energy_island_production', 'Energy Production', '{"x":0,"y":0,"w":12,"h":1}', '{"icon_visibility":false, "background":"none", "template":true}', 'single', 2 ),
( 2, 'energy_share', 'Energy share', '{"x":5,"y":1,"w":2,"h":1}', '{"icon_visibility":false, "background": "none"}', 'single', 2 ),
( 3, 'electricity', 'Electricity', '{"x":0,"y":1,"w":5,"h":10}', '{"icon":"electricity", "background":"linear-gradient(to right, #343638, #4ea5c5a0, #343638)"}', 'doughnut', 2 ),
( 4, 'heat', 'Heat', '{"x":7,"y":1,"w":5,"h":10}', '{"icon":"heat", "background":"linear-gradient(to right, #343638, #8e6faaa0, #343638)"}', 'doughnut', 2 ),
( 5, 'renewables', 'Renewables', '{"x":5,"y":2,"w":2,"h":3}', '{"icon":"renewables", "background":"radial-gradient #7ece9b80, #343638)"}','single', 2 ),
( 6, 'non_renewables', 'Fosil Fuels', '{"x":5,"y":4,"w":2,"h":3}', '{"icon":"fossil_fuels", "background":"radial-gradient(#c9595980, #343638)"}', 'single', 2 ),
( 7, 'external', 'External', '{"x":5,"y":6,"w":2,"h":3}', '{"icon":"import", "background":"radial-gradient(#d4bc6b80, #343638)"}', 'single', 2 ),
/* CONSUMPTION SCREEN */
( 10, 'energy_island_import', 'Energy import', '{"x":0,"y":0,"w":12,"h":1}', '{"icon_visibility":false, "background":"none", "template":true}', 'single', 1 ),
( 11, 'office', 'Office', '{"x":3,"y":1,"w":3,"h":9}', '{"icon":"office", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
( 12, 'supercomputer', 'Supercomputers', '{"x":6,"y":1,"w":3,"h":9}', '{"icon":"supercomputer", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
( 13, 'hvac', 'HVAC', '{"x":9,"y":1,"w":3,"h":9}', '{"icon":"hvac", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
( 14, 'heat_exchanged', 'Heat exchanged', '{"x":0,"y":1,"w":3,"h":9}', '{"icon":"heat", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
/* RENEWABILITY SCREEN */
(20, 'heat_renewability', 'Heat renewability', null, '{"icon":"renewability"}', 'knob', null),
(21, 'heat_renewability', 'Heat renewability', null, '{"icon":"renewability"}', 'knob', null);

INSERT INTO information_tile_measurement ( id, measurement_id, asset_category_id, domain, direction, measurement_type_id, sensor_name, measurement_name, information_tile_id, aggregation_function ) VALUES
( 1, 84, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'sum' ),
( 2, 85, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'sum' ),
( 3, 86, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'sum' ),
( 4, 87, NULL, NULL, NULL, NULL, NULL, NULL, 4, 'sum' ),
( 5, 88, NULL, NULL, NULL, NULL, NULL, NULL, 4, 'sum' ),
( 6, 89, NULL, NULL, NULL, NULL, NULL, NULL, 4, 'sum' ),
( 7, 90, NULL, NULL, NULL, NULL, NULL, NULL, 5, 'sum' ),
( 8, 91, NULL, NULL, NULL, NULL, NULL, NULL, 6, 'sum' ),
( 9, 92, NULL, NULL, NULL, NULL, NULL, NULL, 7, 'sum' ),
( 10, 65, NULL, NULL, NULL, NULL, NULL, NULL, 11, 'sum' ),
( 11, 67, NULL, NULL, NULL, NULL, NULL, NULL, 11, 'sum' ),
( 12, 69,NULL, NULL, NULL, NULL, NULL, NULL, 12, 'sum' ),
( 13, 71, NULL, NULL, NULL, NULL, NULL, NULL, 12, 'sum' ),
( 14, 73, NULL, NULL, NULL, NULL, NULL, NULL, 13, 'sum' ),
( 15, 75, NULL, NULL, NULL, NULL, NULL, NULL, 13, 'sum' ),
( 16, 77, NULL, NULL, NULL, NULL, NULL, NULL, 14, 'sum' ),
( 17, 79, NULL, NULL, NULL, NULL, NULL, NULL, 14, 'sum' ),
( 18, 100, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'mean' ),
( 19, 101, NULL, NULL, NULL, NULL, NULL, NULL, 10, 'mean' ),
( 20, 110, NULL, NULL, NULL, NULL, NULL, NULL, 20, 'last' ),
( 21, 111, NULL, NULL, NULL, NULL, NULL, NULL, 21, 'last' );

/* CONNECT ASSETS WITH PANELS */
INSERT INTO asset_panel ( panel_id, asset_id ) VALUES
( 1, 3 ),
( 2, 3 );

/* USER DEMANDS DEFINITIONS */
INSERT INTO user_demand_definition ( id, action, message, action_type, information_tile_id ) VALUES
( 1, 'INCREASE_TEMPERATURE', 'Please, increase the temperature', 'INCREASE', 20 ),
( 2, 'DECREASE_TEMPERATURE', 'Please, decrease the temperature', 'DECREASE', 20 ),
( 3, 'INCREASE_TEMPERATURE', 'Please, increase the temperature', 'INCREASE', 21 ),
( 4, 'DECREASE_TEMPERATURE', 'Please, decrease the temperature', 'DECREASE', 21 );

/* USER DEMAND SCHEDULES */
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, update_date ) VALUES
( 1, 5, 1, now(), now() + interval '48 hour', now() ),
( 2, 6, 2, now(), now() + interval '48 hour', now() ),
( 3, 6, 2, now() - interval '96 hour', now() - interval '24 hour', now() );

/* SET COLORS */
INSERT INTO public.measurement_details(
  id, key, value, measurement_id)
	SELECT  m.id, 'color', mt.color, m.id
	FROM measurement m JOIN measurement_type mt on m.measurement_type_id = mt.id;

UPDATE measurement_details
    SET value =
    CASE
    WHEN measurement.name = 'renewable' THEN '#4ADE80'
    WHEN measurement.name = 'non_renewable' THEN '#E52122'
    WHEN measurement.name = 'external' THEN '#EAB403'
    WHEN measurement.domain = 'electricity'  THEN '#0BA6DF'
    WHEN measurement.domain = 'heat'  THEN '#A855F3'
    ELSE '9B59B6'
 END 
 FROM measurement WHERE  measurement.id = measurement_details.measurement_id 
AND measurement_details."key" = 'color';