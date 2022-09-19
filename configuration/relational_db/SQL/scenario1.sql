
INSERT INTO uuid VALUES ('2');

/* CREATE ASSET CATEGORIES */
INSERT INTO asset_category ( id, name, description )
    VALUES( 1, 'Boiler', NULL );
INSERT INTO asset_category ( id, name, description )
    VALUES( 2, 'Photovoltaic Panel', NULL );

/* CREATE USERS */
INSERT INTO users ( id, uuid ) 
    VALUES( 1, '2' );
INSERT INTO users ( id, uuid ) 
    VALUES( 2, '2' );

INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid ) 
    VALUES( 1, 'user1', 'User 1', 18, 1, '2' );
INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid ) 
    VALUES( 2, 'user2', 'User 2', 18, 2, '2' );

/* CREATE ISLAND */
INSERT INTO asset ( id, name, label, asset_type_id, uuid ) 
    VALUES( 3, 'energy_island', 'Energy Island', 4, '2' );

/* CREATE BUILDINGS */
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 4, 'building2', 'Building 2', 3, 3, '2' );
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 5, 'flat1', 'Flat 1', 2, 4, '2' );
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 6, 'building1', 'Building 1', 3, 3, '2' );

/* CREATE ENERGY ASSETS */
INSERT INTO asset ( id, name, label, asset_type_id, uuid, asset_category_id )
    VALUES( 7, 'gas_boiler1', 'Gas Boiler 1', 7, '2', 1 );
INSERT INTO asset ( id, name, label, asset_type_id, uuid, asset_category_id )
    VALUES( 8, 'gas_boiler2', 'Gas Boiler 2', 7, '2', 1 );
INSERT INTO asset ( id, name, label, asset_type_id, uuid, asset_category_id )
    VALUES( 9, 'solar_collector1', 'Solar Collector 1', 13, '2', 2 );

/* CONNECT ASSETS */
-- Conecction_type:
--   0 owner
--   1 resident
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 1, 5, 0 );
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 2, 2, 5, 1 );
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 3, 2, 6, 0 );
    
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 4, 5, 7, NULL );
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 5, 6, 8, NULL );
INSERT INTO asset_connection ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 6, 6, 9, NULL );

/* CREATE MEASUREMENTS */
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 21, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 5, '2' );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 22, 'renewability', 'Renewability', 5, 'heat', NULL, 'renewability', 6, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 23, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 5, '2' );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 24, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 6, '2' );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 25, 'heat_consumed', 'Heat Consumed', 7, 'heat', 'in', 'thermostat', 4, '2' );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 26, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'gas_boiler', 7, '2' );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 27, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'gas_boiler', 8, '2' );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 28, 'heat_supply', 'Heat Supply', 7, 'heat', 'out', 'solar_collector', 9, '2' );

/* TILES AND PANLES */
INSERT INTO information_panel ( id, name, label, uuid ) 
    VALUES( 1, 'panel1', 'Panel 1', '2' );

INSERT INTO information_tile ( id, name, label, type, layout, information_panel_id ) 
    VALUES( 1, 'renewability', 'Renewability', 'knob', NULL, 1 );

INSERT INTO information_tile_measurement ( id, measurement_id, domain, direction, measurement_type_id, sensor_name, information_tile_id ) 
    VALUES( 1, 1, NULL, NULL, NULL, NULL, 1 );
INSERT INTO information_tile_measurement ( id, measurement_id, domain, direction, measurement_type_id, sensor_name, information_tile_id ) 
    VALUES( 2, 2, NULL, NULL, NULL, NULL, 1 );

/* CONNECT ASSETS WITH PANELS */
INSERT INTO asset_panel ( panel_id, asset_id )
    VALUES( 1, 5 );
INSERT INTO asset_panel ( panel_id, asset_id )
    VALUES( 1, 6 );

/* USER DEMANDS DEFINITIONS */
INSERT INTO user_demand_definition ( id, action, message, action_type ) 
    VALUES( 1, 'INCREASE_TEMPERATURE', 'Please, increase the temperature', 'INCREASE' );
INSERT INTO user_demand_definition ( id, action, message, action_type ) 
    VALUES( 2, 'DECREASE_TEMPERATURE', 'Please, decrease the temperature', 'DECREASE' );

/* USER DEMAND SCHEDULES */
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, update_date ) 
    VALUES( 1, 5, 1, now(), now() + interval '1 hour', now() );
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, update_date ) 
    VALUES( 2, 6, 2, now(), now() + interval '1 hour', now() );
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, update_date ) 
    VALUES( 3, 6, 2, now() - interval '2 hour', now() - interval '1 hour', now() );

COMMIT;