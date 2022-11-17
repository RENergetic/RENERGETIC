
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
INSERT INTO users ( id, uuid ) VALUES
( 1, '2' ),
( 2, '2' );

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
( 16, 'solar_collector1', 'Solar Collector 1', 13, '2', NULL );

/* CONNECT ASSETS */
-- Conecction_type:
--   0 owner
--   1 resident
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type ) VALUES
( 1, 1, 3, 0 ),
( 2, 2, 3, 0 ),
( 3, 1, 5, 0 ),
( 4, 2, 5, 1 ),
( 5, 2, 6, 0 ),
( 6, 1, 7, 0 ),
( 7, 2, 7, 0 );
    
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type ) VALUES
( 14, 5, 7, NULL ),
( 15, 6, 8, NULL ),
( 16, 6, 9, NULL );

/* CREATE MEASUREMENTS */
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) VALUES
( 21, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 5, '2' ),
( 22, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 6, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) VALUES
( 23, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 5, '2' ),
( 24, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 6, '2' ),
( 25, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 4, '2' );

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
( 50, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, 3, NULL, '2' ),
( 51, 'energy_consumed', 'Current energy', 2, 'electricity', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 52, 'renewability', 'Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 3, NULL, '2' ),
( 53, 'renewability', 'Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 3, NULL, '2' ),
( 54, 'non_renewability', 'Non Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 3, NULL, '2' ),
( 55, 'non_renewability', 'Non Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 3, NULL, '2' ),
( 56, 'renewability', 'Renewability', 2, 'none', 'in', 'report', NULL, 3, NULL, '2' ),
( 57, 'renewability', 'Renewability', 5, 'none', 'in', 'report', NULL, 3, NULL, '2' ),
( 58, 'non_renewability', 'Non Renewability', 2, 'none', 'in', 'report', NULL, 3, NULL, '2' ),
( 59, 'non_renewability', 'Non Renewability', 5, 'none', 'in', 'report', NULL, 3, NULL, '2' ),
( 60, 'renewability', 'Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 61, 'renewability', 'Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 62, 'non_renewability', 'Non Renewability', 2, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
( 63, 'non_renewability', 'Non Renewability', 5, 'electricity', 'in', 'energy_meter', NULL, 7, NULL, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 1 */
( 64, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, NULL, 1, '2' ),
( 65, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, NULL, 1, '2' ),
( 66, 'current_power', 'Current power', 1, 'heat', 'in', 'energy_meter', NULL, NULL, 1, '2' ),
( 67, 'energy_consumed', 'Current energy', 2, 'heat', 'in', 'energy_meter', NULL, NULL, 1, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 2 */
( 68, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, NULL, 2, '2' ),
( 69, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, NULL, 2, '2' ),
( 70, 'current_power', 'Current power', 1, 'heat', 'in', 'energy_meter', NULL, NULL, 2, '2' ),
( 71, 'energy_consumed', 'Current energy', 2, 'heat', 'in', 'energy_meter', NULL, NULL, 2, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 3 */		  
( 72, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, NULL, 3, '2' ),
( 73, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, NULL, 3, '2' ),
( 74, 'current_power', 'Current power', 1, 'heat', 'in', 'energy_meter', NULL, NULL, 3, '2' ),
( 75, 'energy_consumed', 'Current energy', 2, 'heat', 'in', 'energy_meter', NULL, NULL, 3, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 4 */
( 76, 'current_power', 'Current power', 1, 'electricity', 'in', 'energy_meter', NULL, NULL, 4, '2' ),
( 77, 'energy_consumed', 'Current energy', 2, 'electricity', 'in', 'energy_meter', NULL, NULL, 4, '2' ),
( 78, 'current_power', 'Current power', 1, 'heat', 'in', 'energy_meter', NULL, NULL, 4, '2' ),
( 79, 'energy_consumed', 'Current energy', 2, 'heat', 'in', 'energy_meter', NULL, NULL, 4, '2' ),
/* MEASUREMENTS TO ASSET CATEGORY 5 */
( 80, 'current_power', 'Current power', 1, 'heat', 'out', 'energy_meter', NULL, NULL, 5, '2' ),
( 81, 'energy_consumed', 'Current energy', 2, 'heat', 'out', 'energy_meter', NULL, NULL, 5, '2' ),
( 82, 'heat_exhanged', 'Heat exhanged', 1, 'heat', 'out', 'heat_exhanger', NULL, NULL, 5, '2' ),
( 83, 'heat_wasted', 'Heat wasted', 1, 'heat', 'out', 'heat_exhanger', NULL, NULL, 5, '2' ),
/* RENEWABILITY MEASUREMENTS FOR ENTIRE ISLAND */
( 84, 'renewability', 'Renewable', 2, 'electricity', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 85, 'non_renewability', 'Fosil Fuels', 2, 'electricity', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 86, 'external', 'External', 2, 'electricity', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 87, 'renewability', 'Renewable', 2, 'heat', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 88, 'non_renewability', 'Fosil Fuels', 2, 'heat', 'out', 'energy_meter', NULL, 3, NULL, '2' ),
( 89, 'external', 'External', 2, 'heat', 'out', 'energy_meter', NULL, 3, NULL, '2' );

/* TILES AND PANELS */
INSERT INTO information_panel ( id, name, label, is_template, featured, uuid ) VALUES
( 1, 'energy_flow', 'Energy flow', false, true, '2' ),
( 2, 'renewability_panel_template', 'Renewability panel for {asset}', true, true, '2' );

INSERT INTO information_tile ( id, name, label, layout, props, type, information_panel_id ) VALUES
/* PRODUCTION SCREEN */
( 1, 'energy_share', 'Energy share', '{"x":5,"y":0,"w":2,"h":1}', '{"icon_visibility":false}', 'single', 2 ),
( 2, 'electricity', 'Electricity', '{"x":0,"y":0,"w":5,"h":10}', '{"icon":"electricity"}', 'doughnut', 2 ),
( 3, 'heat', 'Heat', '{"x":7,"y":0,"w":5,"h":10}', '{"icon":"heat"}', 'doughnut', 2 ),
( 4, 'renewables', 'Renewables', '{"x":5,"y":1,"w":2,"h":3}', '{"icon":"electricity"}','single', 2 ),
( 5, 'waste', 'Waste', '{"x":5,"y":3,"w":2,"h":3}', '{"icon":"electricity"}', 'single', 2 ),
( 6, 'non_renewables', 'Non Renewables', '{"x":5,"y":5,"w":2,"h":3}', '{"icon":"electricity"}', 'single', 2 ),
/* CONSUMPTION SCREEN */
( 10, 'overview', 'PSNC overview', '{"x":0,"y":0,"w":12,"h":1}', '{"icon_visibility":false}', 'single', 1 ),
( 11, 'office', 'Office', '{"x":3,"y":1,"w":3,"h":9}', '{"icon":"office"}', 'multi_knob', 1 ),
( 12, 'supercomputer', 'Supercomputers', '{"x":6,"y":1,"w":3,"h":9}', '{"icon":"supercomputer"}', 'multi_knob', 1 ),
( 13, 'hvac', 'HVAC', '{"x":9,"y":1,"w":3,"h":9}', '{"icon":"hvac"}', 'multi_knob', 1 ),
( 14, 'heat_exhanged', 'Heat exchanged', '{"x":0,"y":1,"w":3,"h":9}', '{"icon":"heat"}', 'multi_knob', 1 ),
/* TODO: where are this tiles shown? */
( 20, 'heat', 'Heat', null, '{"icon":"heat"}', 'single', null ),
( 21, 'heat', 'Heat', null, '{"icon":"heat"}', 'multi_knob', null );

INSERT INTO information_tile_measurement ( id, measurement_id, asset_category_id, domain, direction, measurement_type_id, sensor_name, measurement_name, information_tile_id ) VALUES
( 1, 84, NULL, NULL, NULL, NULL, NULL, NULL, 2 ),
( 2, 85, NULL, NULL, NULL, NULL, NULL, NULL, 2 ),
( 3, 86, NULL, NULL, NULL, NULL, NULL, NULL, 2 ),
( 4, 87, NULL, NULL, NULL, NULL, NULL, NULL, 3 ),
( 5, 88, NULL, NULL, NULL, NULL, NULL, NULL, 3 ),
( 6, 89, NULL, NULL, NULL, NULL, NULL, NULL, 3 ),
( 7, 57, NULL, NULL, NULL, NULL, NULL, NULL, 4 ),
( 8, 45, NULL, NULL, NULL, NULL, NULL, NULL, 5 ),
( 9, 59, NULL, NULL, NULL, NULL, NULL, NULL, 6 ),
( 10, 64, NULL, NULL, NULL, NULL, NULL, NULL, 11 ),
( 11, 66, NULL, NULL, NULL, NULL, NULL, NULL, 11 ),
( 12, 68,NULL, NULL, NULL, NULL, NULL, NULL, 12 ),
( 13, 70, NULL, NULL, NULL, NULL, NULL, NULL, 12 ),
( 14, 72, NULL, NULL, NULL, NULL, NULL, NULL, 13 ),
( 15, 74, NULL, NULL, NULL, NULL, NULL, NULL, 13 ),
( 16, 76, NULL, NULL, NULL, NULL, NULL, NULL, 14 ),
( 17, 78, NULL, NULL, NULL, NULL, NULL, NULL, 14 );

/* CONNECT ASSETS WITH PANELS */
INSERT INTO asset_panel ( panel_id, asset_id ) VALUES
( 1, 3 ),
( 2, 3 ),
( 2, 7 );

/* USER DEMANDS DEFINITIONS */
INSERT INTO user_demand_definition ( id, action, message, action_type, information_tile_id ) VALUES
( 1, 'INCREASE_TEMPERATURE', 'Please, increase the temperature', 'INCREASE', 20 ),
( 2, 'DECREASE_TEMPERATURE', 'Please, decrease the temperature', 'DECREASE', 21 );

/* USER DEMAND SCHEDULES */
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, update_date ) VALUES
( 1, 5, 1, now(), now() + interval '48 hour', now() ),
( 2, 6, 2, now(), now() + interval '48 hour', now() ),
( 3, 6, 2, now() - interval '96 hour', now() - interval '24 hour', now() );