
INSERT INTO uuid VALUES ('1');

/* CREATE MEASUREMENTS TO CHECK INFLUXDB MEASUREMENTS NAMES */
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 1, 'renewability', 5 , 'renewability', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 2, 'heat_pump', 3 , 'heat_pump', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 3, 'energy_meter', 8 , 'energy_meter', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 4, 'pv', 3 , 'pv', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 5, 'battery', 8 , 'battery', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 6, 'hot_water', 4 , 'hot_water', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 7, 'cold_water', 4 , 'cold_water', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 8, 'weather', 4 , 'weather', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9, 'heat_exchange', 3 , 'heat_exchange', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 10, 'cooling_circuits', 3 , 'cooling_circuits', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 11, 'chiller', 4 , 'chiller', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 12, 'heat_meter', 4 , 'heat_meter', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 13, 'dh_temperature', 4 , 'dh_temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 14, 'dhw_temperature', 4 , 'dhw_temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 15, 'tapping_water', 3 , 'tapping_water', 1 );
 INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 16, 'thermostate', 4 , 'thermostate', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 17, 'temperature', 4 , 'temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 18, 'cpu', 4 , 'cpu', 1 );

/* CREATE TAGS TO CHECK INFLUXDB TAGS */
INSERT INTO tags ( id, key, value ) VALUES( 1, 'asset_name', null );
INSERT INTO tags ( id, key, value ) VALUES( 2, 'prediction_window', '\\d+[Mdhms]' );
INSERT INTO tags ( id, key, value ) VALUES( 3, 'predictive_model', null );
INSERT INTO tags ( id, key, value ) VALUES( 4, 'direction', '(?i)((in)|(out)|(none))' );
INSERT INTO tags ( id, key, value ) VALUES( 5, 'domain', '(?i)((heat)|(electricity))' );

COMMIT;