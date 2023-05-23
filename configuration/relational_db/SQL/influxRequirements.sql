
INSERT INTO uuid VALUES ('1');

/* CREATE MEASUREMENTS TO CHECK INFLUXDB MEASUREMENTS NAMES */
INSERT INTO measurement 
( id, name, measurement_type_id, sensor_name, uuid ) VALUES
( 991, 'renewability', 5 , 'renewability', 1 ),
( 992, 'heat_pump', 3 , 'heat_pump', 1 ),
( 993, 'energy_meter', 8 , 'energy_meter', 1 ),
( 994, 'pv', 3 , 'pv', 1 ),
( 995, 'battery', 8 , 'battery', 1 ),
( 996, 'hot_water', 4 , 'hot_water', 1 ),
( 997, 'cold_water', 4 , 'cold_water', 1 ),
( 998, 'weather', 4 , 'weather', 1 ),
( 999, 'heat_exchange', 3 , 'heat_exchange', 1 ),
( 1000, 'cooling_circuits', 3 , 'cooling_circuits', 1 ),
( 1001, 'chiller', 4 , 'chiller', 1 ),
( 1002, 'heat_meter', 4 , 'heat_meter', 1 ),
( 1003, 'electricity_meter', 4 , 'electricity_meter', 1 ),
( 1004, 'dh_temperature', 4 , 'dh_temperature', 1 ),
( 1005, 'dhw_temperature', 4 , 'dhw_temperature', 1 ),
( 1006, 'tapping_water', 3 , 'tapping_water', 1 ),
( 1007, 'thermostate', 4 , 'thermostate', 1 ),
( 1008, 'temperature', 4 , 'temperature', 1 ),
( 1009, 'cpu', 4 , 'cpu', 1 ),
( 1010, 'report', 5 , 'report', 1 );

/* CREATE TAGS TO CHECK INFLUXDB TAGS */
INSERT INTO tags ( id, key, value ) VALUES
( 1, 'measurement_type', null ),
( 2, 'category', null ),
( 3, 'predictive_model', null ),
( 4, 'prediction_window', '\\d+[Mdhms]' ),
( 5, 'asset_name', null ),
( 6, 'direction', '(?i)((in)|(out)|(none))' ),
( 7, 'domain', '(?i)((heat)|(electricity))' ),
( 8, 'type_data', '(?i)((simulated)|(real))' ),
( 9, 'parameter', null ),
( 10, 'sensor_id', null ),
( 11, 'interpolation_method', null ),
( 12, 'facility_description', null );