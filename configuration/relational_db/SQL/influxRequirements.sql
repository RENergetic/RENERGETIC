
INSERT INTO uuid VALUES ('1');

/* CREATE MEASUREMENTS TO CHECK INFLUXDB MEASUREMENTS NAMES */
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 991, 'renewability', 5 , 'renewability', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 992, 'heat_pump', 3 , 'heat_pump', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 993, 'energy_meter', 8 , 'energy_meter', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 994, 'pv', 3 , 'pv', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 995, 'battery', 8 , 'battery', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 996, 'hot_water', 4 , 'hot_water', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 997, 'cold_water', 4 , 'cold_water', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 998, 'weather', 4 , 'weather', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 999, 'heat_exchange', 3 , 'heat_exchange', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9910, 'cooling_circuits', 3 , 'cooling_circuits', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9911, 'chiller', 4 , 'chiller', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9912, 'heat_meter', 4 , 'heat_meter', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9913, 'dh_temperature', 4 , 'dh_temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9914, 'dhw_temperature', 4 , 'dhw_temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9915, 'tapping_water', 3 , 'tapping_water', 1 );
 INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9916, 'thermostate', 4 , 'thermostate', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9917, 'temperature', 4 , 'temperature', 1 );
INSERT INTO measurement ( id, name, measurement_type_id, sensor_name, uuid )
 VALUES( 9918, 'cpu', 4 , 'cpu', 1 );

/* CREATE TAGS TO CHECK INFLUXDB TAGS */


INSERT INTO tags ( id, key, value ) VALUES( 1, 'measurement_type', null );
INSERT INTO tags ( id, key, value ) VALUES( 2, 'category', null ); 

INSERT INTO tags ( id, key, value ) VALUES( 4, 'prediction_window', '\\d+[Mdhms]' );
INSERT INTO tags ( id, key, value ) VALUES( 3, 'predictive_model', null );
INSERT INTO tags ( id, key, value ) VALUES( 21, 'asset_name', null );
INSERT INTO tags ( id, key, value ) VALUES( 22, 'direction', '(?i)((in)|(out)|(none))' );
INSERT INTO tags ( id, key, value ) VALUES( 23, 'domain', '(?i)((heat)|(electricity))' );

COMMIT;