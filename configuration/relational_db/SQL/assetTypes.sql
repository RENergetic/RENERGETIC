
INSERT INTO asset_type 
( id, name, label, category, renovable ) VALUES
( 1, 'room', 'Room', 'structural', NULL ),
( 2, 'flat', 'Flat', 'structural', NULL ),
( 5, 'generation_plant', 'Generation plant', 'energy', 50 ),
( 6, 'heat_pump', 'Heat pump', 'energy', 40 ),
( 7, 'gas_boiler', 'Gas boiler', 'energy', 0 ),
( 8, 'co-generation_unit', 'Co-generation unit', 'energy', 0 ),
( 9, 'coal_plant', 'Coal plant', 'energy', 0 ),
( 11, 'external_heat_grid', 'External heat grid', 'energy', NULL ),
( 12, 'external_electricity_grid', 'External electricity grid', 'energy', NULL ),
( 13, 'solar_thermal_collector', 'Solar thermal collector', 'energy', 100 ),
( 14, 'steam', 'Steam', 'infrastructure', NULL ),
( 15, 'district_heating', 'District heating', 'infrastructure', NULL ),
( 16, 'district_cooling', 'District cooling', 'infrastructure', NULL ),
( 17, 'electricity', 'Electricity', 'infrastructure', NULL ),
( 19, 'ev_charging_station', 'Ev charging station', 'energy', 100 ),
( 3, 'building', 'Building', 'structural', NULL ),
( 18, 'user', 'User', 'user', NULL ),
( 4, 'energy_island', 'Energy island', 'structural', NULL ),
( 10, 'pv_plant', 'PV plant', 'energy', 100 ),
( 20, 'office', 'Office', 'building', NULL ),
( 21, 'hvac', 'HVAC', 'infrastructure', NULL ),
( 22, 'supercomputer', 'Supercomputer', 'infrastructure', NULL ),
( 23, 'datacenter', 'datacenter', 'building', NULL ),
( 24, 'heat_exchange', 'heat_exchange', 'infrastructure', NULL ); 
 