
INSERT INTO uuid VALUES 2

/* CREATE USERS */
INSERT INTO users ( id, name, uuid ) 
    VALUES( 1, "user1", "2" );
INSERT INTO users ( id, name, uuid ) 
    VALUES( 2, "user2", "2" );

INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid ) 
    VALUES( 1, "user1", "User 1", 18, 1, "2" );
INSERT INTO asset ( id, name, label, asset_type_id, user_id, uuid ) 
    VALUES( 2, "user2", "User 2", 18, 2, "2" );

/* CREATE ISLAND */
INSERT INTO asset ( id, name, label, asset_type_id, uuid ) 
    VALUES( 3, "energy_island", "Energy Island", 4, "2" );

/* CREATE BUILDINGS */
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 4, "building2", "Building 2", 3, 3, "2" );
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 5, "flat1", "Flat 1", 2, 4, "2" );
INSERT INTO asset ( id, name, label, asset_type_id, parent_asset_id, uuid ) 
    VALUES( 6, "building1", "Building 1", 3, 3, "2" );

/* CREATE ENERGY ASSETS */
INSERT INTO asset ( id, name, label, asset_type_id, uuid ) 
    VALUES( 7, "gas_boiler1", "Gas Boiler 1", 7, "2" );
INSERT INTO asset ( id, name, label, asset_type_id, uuid ) 
    VALUES( 8, "gas_boiler2", "Gas Boiler 2", 7, "2" );
INSERT INTO asset ( id, name, label, asset_type_id, uuid ) 
    VALUES( 9, "solar_collector1", "Solar Collector 1", 13, "2" );

/* CONNECT ASSETS */
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 1, 5, "owner" )
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 2, 5, "resident" )
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 2, 6, "owner" )
    
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 5, 7, NULL )
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 6, 8, NULL )
INSERT INTO asset_panel ( id, asset_id, connected_asset_id, connection_type )
    VALUES( 1, 6, 9, NULL )

/* CREATE MEASUREMENTS */
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 1, "renewability", "Renewability", 5, "heat", NULL, "renewability", 5, "2" );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 2, "renewability", "Renewability", 5, "heat", NULL, "renewability", 6, "2" );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 3, "heat_consumed", "Heat Consumed", 7, "heat", "in", "thermostat", 5, "2" );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 4, "heat_consumed", "Heat Consumed", 7, "heat", "in", "thermostat", 6, "2" );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 5, "heat_consumed", "Heat Consumed", 7, "heat", "in", "thermostat", 4, "2" );

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 6, "heat_supply", "Heat Supply", 7, "heat", "out", "gas_boiler", 7, "2" );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 7, "heat_supply", "Heat Supply", 7, "heat", "out", "gas_boiler", 8, "2" );
INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, asset_id, uuid ) 
    VALUES( 8, "heat_supply", "Heat Supply", 7, "heat", "out", "solar_collector", 9, "2" );

/* TILES AND PANLES */
INSERT INTO information_panel ( id, name, label, uuid ) 
    VALUES( 1, "panel1", "Panel 1", "2" );

INSERT INTO information_tile ( id, name, label, type, layout, information_panel_id ) 
    VALUES( 1, "renewability", "Renewability", "knob", NULL, 1 );

INSERT INTO information_tile_measurement ( id, measurement_id, domain, direction, measurement_type_id, sensor_name, information_tile_id ) 
    VALUES( 1, 1, NULL, NULL, NULL, NULL, 1 );
INSERT INTO information_tile_measurement ( id, measurement_id, domain, direction, measurement_type_id, sensor_name, information_tile_id ) 
    VALUES( 2, 2, NULL, NULL, NULL, NULL, 1 );

/* CONNECT ASSETS WITH PANELS */
INSERT INTO asset_panel ( panel_id, asset_id )
    VALUES( 1, 5 )
INSERT INTO asset_panel ( panel_id, asset_id )
    VALUES( 1, 6 )

/* USER DEMANDS DEFINITIONS */
INSERT INTO user_demand_definition ( id, action, message, action_type ) 
    VALUES( 1, "INCREASE_TEMPERATURE", "Please, increase the temperature", "INCREASE" );
INSERT INTO user_demand_definition ( id, action, message, action_type ) 
    VALUES( 2, "DECREASE_TEMPERATURE", "Please, decrease the temperature", "DECREASE" );

/* USER DEMAND SCHEDULES */
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, demand_update ) 
    VALUES( 1, 5, 1, now(), now() + interval "1 hour", now() );
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, demand_update ) 
    VALUES( 1, 6, 2, now(), now() + interval "1 hour", now() );
INSERT INTO user_demand_schedule ( id, asset_id, demand_id, demand_start, demand_stop, demand_update ) 
    VALUES( 1, 6, 2, now() - interval "2 hour", now() - interval "1 hour", now() );

COMMIT;