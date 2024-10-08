INSERT INTO public.asset_type(
	id, label, name, renovable)
	VALUES (28, 'Energy Consumer', 'energy_consumer', NULL),
	(29, 'Energy Storage', 'energy_storage', NULL),
	(30, 'Optimizer Domain', 'optimizer_domain', NULL);


UPDATE public.asset_type
	SET label='Generation', name='Generation'
	WHERE name = 'generation_plant';


INSERT INTO public.asset_category(
	id, description, label, name)
	VALUES (1, '', 'Heating', 'heating'),
	(2, '', 'Charging Stations', 'charging_station'),
	(3, '', 'Water Treatment', 'water_treatment');


INSERT INTO public.asset(
	id, name, geo_location, label, asset_category_id, parent_asset_id, asset_type_id, user_id, uuid)
	VALUES (1000, 'energy_island', NULL, 'Energy Island', NULL, null, 4, NULL, 1),
	(1001, 'electricity_domain', NULL, 'Electricity Domain', NULL, 1000, 30, NULL, 1),
	(1002, 'heat_domain', NULL, 'Heat Domain', NULL, 1000, 30, NULL, 1),
	(1003, 'pv_panels', NULL, 'PV Panels', NULL, 1000, 10, NULL, 1),
	-- (1100, 'de-nieuwe-dokken-total', NULL, 'de Nieuwe Dokken Total', NULL, 1000, ???, NULL, 1),
	(1101, 'de-nieuwe-dokken-charging-stations', NULL, 'Energy demand of the charging stations at De Nieuwe Dokken', 2, 1000, 19, NULL, 1),
	(1102, 'de-nieuwe-dokken-vacuumnet', NULL, 'Energy demand of the vacuumnet (vacuum pumps) at De Nieuwe Dokken', 3, 1000, 28, NULL, 1),
	(1103, 'de-nieuwe-dokken-districtheating', NULL, 'Energy demand of the district heating network (e.g., pump energy) at De Nieuwe Dokken', 1, 1000, 15, NULL, 1),
	(1104, 'de-nieuwe-dokken-heatpump', NULL, 'Thermal energy produced by the community-level heat pump at De Nieuwe Dokken', 1, 1000, 6, NULL, 1),
	(1105, 'de-nieuwe-dokken-watertreatment', NULL, 'Energy demand of the water treatment plant at De Nieuwe Dokken', 3, 1000, 28, NULL, 1),
	(1106, 'de-nieuwe-dokken-gas-christeyns', NULL, 'Thermal energy production of the gas boilers installed at Christyens and delivering heat to De Nieuwe Dokken', NULL, 1000, 5, NULL, 1),
	(1107, 'de-nieuwe-dokken-wasteheat-christeyns', NULL, 'Waste heat thermal energy production at Christeyns and delivered to De Nieuwe Dokken', NULL, 1000, 5, NULL, 1),
	(1108, 'de-nieuwe-dokken-total-heat-christeyns', NULL, 'de Nieuwe Dokken Total Heat Christeyns', NULL, 1000, 5, NULL, 1),
	(1109, 'de-nieuwe-dokken-total-heat', NULL, 'Total thermal energy demand at De Nieuwe Dokken', 1, 1000, 28, NULL, 1),
	(1110, 'de-nieuwe-dokken-pv-017A-xxxxx9A1', NULL, 'PV power produced by the PV field with installation code 017A-xxxxx9A1 at De Nieuwe Dokken', NULL, 1003, 10, NULL, 1),
	(1111, 'de-nieuwe-dokken-pv-0198-xxxxx4B4', NULL, 'PV power produced by the PV field with installation code 0198-xxxxx4B4 at De Nieuwe Dokken', NULL, 1003, 10, NULL, 1),
	(1112, 'de-nieuwe-dokken-pv-0198-xxxxx853', NULL, 'PV power produced by the PV field with installation code 0198-xxxxx853 at De Nieuwe Dokken', NULL, 1003, 10, NULL, 1),
	(1113, 'de-nieuwe-dokken-pv-0198-xxxxx9C0', NULL, 'PV power produced by the PV field with installation code 0198-xxxxx9C0 at De Nieuwe Dokken', NULL, 1003, 10, NULL, 1),
	(1114, 'de-nieuwe-dokken-battery', NULL, 'Battery Storage', NULL, 1000, 29, NULL, 1),
	(1115, 'de-nieuwe-dokken-biogas', NULL, 'Thermal energy production of the de-nieuwe-dokken-biogas', NULL, 1000, 5, NULL, 1);


INSERT INTO public.asset_details(
	id, key, value, asset_id)
	VALUES (1, 'type', 'supplier', 1003),
	(2, 'renewability', 'renewable', 1003),
	(3, 'action', 'non_control', 1003),
	(4, 'domain', 'electricity', 1003),

	-- (10, 'type', ???, 1100),
	-- (11, 'renewability', 'mixed_renewable', 1100),
	-- (12, 'action', 'non_control', 1100),
	-- (13, 'domain', 'electricity', 1100),
	-- (14, 'Max_load', '100kWh', 1100),
	-- (15, 'Cost', '???', 1100),
	-- (16, 'External', 'true', 1100),

	(20, 'type', 'consumer', 1101),
	(21, 'renewability', 'mixed_renewable', 1101),
	(22, 'action', 'non_control', 1101),
	(23, 'domain', 'electricity', 1101),

	(30, 'type', 'consumer', 1102),
	(31, 'renewability', 'mixed_renewable', 1102),
	(32, 'action', 'non_control', 1102),
	(33, 'domain', 'electricity', 1102),

	(40, 'type', 'consumer', 1103),
	(41, 'renewability', 'mixed_renewable', 1103),
	(42, 'action', 'non_control', 1103),
	(43, 'domain', 'electricity', 1103),

	(50, 'type', 'supplier', 1104),
	(51, 'renewability', 'mixed_renewable', 1104),
	(52, 'action', 'controllable', 1104),
	(53, 'domain', 'heat', 1104),
	(54, 'Mu', '3', 1104),
	(55, 'Max_in', '50kWh', 1104),
	(56, 'Max_out', '150kWh', 1104),

	(60, 'type', 'consumer', 1105),
	(61, 'renewability', 'mixed_renewability', 1105),
	(62, 'action', 'non_control', 1105),
	(63, 'domain', 'electricity', 1105),

	(70, 'type', 'supplier', 1106),
	(71, 'renewability', 'non_renewable', 1106),
	(72, 'action', 'controllable', 1106),
	(73, 'domain', 'heat', 1106),
	(74, 'max_load', '377kWh', 1106),
	(75, 'external', 'true', 1106),

	(80, 'type', 'supplier', 1107),
	(81, 'renewability', 'mixed_renewable', 1107),
	(82, 'action', 'controllable', 1107),
	(83, 'domain', 'heat', 1107),

	-- (90, 'type', ???, 1108),
	-- (91, 'renewability', ???, 1108),
	-- (92, 'action', ???, 1108),
	-- (93, 'domain', 'heat', 1108),

	(100, 'type', 'consumer', 1109),
	(101, 'renewability', 'non_renewable', 1109),
	(102, 'action', 'non_control', 1109),
	(103, 'domain', 'heat', 1109),

	(110, 'type', 'supplier', 1110),
	(111, 'renewability', 'renewable', 1110),
	(112, 'action', 'non_control', 1110),
	(113, 'domain', 'electricity', 1110),

	(120, 'type', 'supplier', 1111),
	(121, 'renewability', 'renewable', 1111),
	(122, 'action', 'non_control', 1111),
	(123, 'domain', 'electricity', 1111),

	(130, 'type', 'supplier', 1112),
	(131, 'renewability', 'renewable', 1112),
	(132, 'action', 'non_control', 1112),
	(133, 'domain', 'electricity', 1112),

	(140, 'type', 'supplier', 1113),
	(141, 'renewability', 'renewable', 1113),
	(142, 'action', 'non_control', 1113),
	(143, 'domain', 'electricity', 1113),

	(150, 'type', 'producer', 1114),
	(151, 'renewability', 'mixed_renewable', 1114),
	(152, 'action', 'controllable', 1114),
	(153, 'domain', 'electricity', 1114),
	(154, 'max_capacity', '192,6kWh', 1114),
	(155, 'initial_state', '0', 1114),
	(156, 'mu', '0,927', 1114),
	(157, 'max_charge', '50kWh', 1114),
	(158, 'max_discharge', '50kWh', 1114);


INSERT INTO public.asset_connection(
	id, asset_id, connected_asset_id, connection_type)
	VALUES (1, 1003, 1001, 'fixed_generation_in'),
	-- (2, 1100, 1001, 'flexible_load_in'),
	(3, 1101, 1001, 'fixed_load_in'),
	(4, 1102, 1001, 'fixed_load_in'),
	(5, 1103, 1001, 'fixed_load_in'),
	(6, 1104, 1001, 'heat_pump_from'),
	(7, 1104, 1002, 'heat_pump_to'),
	(8, 1105, 1001, 'fixed_load_in'),
	(9, 1106, 1002, 'flexible_generation_in'),
	(10, 1107, 1002, 'fixed_generation_in'),
	-- (11, 1108, 1001, 'fixed_generation_in'),
	(12, 1109, 1002, 'fixed_load_in'),
	(13, 1114, 1001, 'storage_in');


INSERT INTO public.measurement(
	id, direction, domain, label, name, sensor_id, sensor_name, asset_id, asset_category_id, energy_island_asset_id, measurement_type_id, uuid)
	VALUES
	-- (2000, 'in', 'electricity', 'energy_meter', 'energy_meter', NULL, 'energy_meter', 1100, NULL, 1000, 2, 1),
	(2001, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', 1101, NULL, 1000, 2, 1),
	(2002, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', 1102, NULL, 1000, 2, 1),
	(2003, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', 1103, NULL, 1000, 2, 1),
	(2004, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', 1104, NULL, 1000, 2, 1),
	(2005, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', 1105, NULL, 1000, 2, 1),
	(2006, 'out', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', 1106, NULL, 1000, 2, 1),
	(2007, 'out', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', 1107, NULL, 1000, 2, 1),
	-- (2008, 'out', 'heat', 'energy_meter', 'current_energy', NULL, 'energy_meter', 1108, NULL, 1000, 2, 1),
	(2009, 'out', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', 1104, NULL, 1000, 2, 1),
	(2010, 'in', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', 1109, NULL, 1000, 2, 1),
	(2011, 'out', 'electricity', 'pv', 'power', NULL, 'pv', 1110, NULL, 1000, 1, 1),
	(2012, 'out', 'electricity', 'pv', 'power', NULL, 'pv', 1111, NULL, 1000, 1, 1),
	(2013, 'out', 'electricity', 'pv', 'power', NULL, 'pv', 1112, NULL, 1000, 1, 1),
	(2014, 'out', 'electricity', 'pv', 'power', NULL, 'pv', 1113, NULL, 1000, 1, 1),
	-- Measurements for categories: Heating
	(2050, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', NULL, 1, 1000, 2, 1),
	(2051, 'out', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', NULL, 1, 1000, 2, 1),
	(2052, 'in', 'heat', 'energy_meter', 'energy', NULL, 'energy_meter', NULL, 1, 1000, 2, 1),
	-- Measurements for categories: Charging stations
	(2053, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', NULL, 2, 1000, 2, 1),
	-- Measurements for categories: Water Treatmeant
	(2054, 'in', 'electricity', 'energy_meter', 'energy', NULL, 'energy_meter', NULL, 3, 1000, 2, 1);



INSERT INTO tags (
	id, key, value )
	VALUES (50, 'type_data', 'real');


INSERT INTO public.measurement_tags(
	measurement_id, tag_id)
	VALUES
	-- (2000, 50),
	(2001, 50),
	(2002, 50),
	(2003, 50),
	(2004, 50),
	(2005, 50),
	(2006, 50),
	(2007, 50),
	-- (2008, 50),
	(2009, 50),
	(2010, 50),
	(2011, 50),
	(2012, 50),
	(2013, 50),
	(2014, 50);


INSERT INTO information_panel ( id, name, label, is_template, featured, uuid ) VALUES
	( 1, 'consumption_screen', 'Energy Consumption', false, false, 1 );


INSERT INTO information_tile ( id, name, label, layout, props, type, information_panel_id ) VALUES
	/* CONSUMPTION SCREEN */
	( 1, 'heating', 'Heating', '{"x":3,"y":1,"w":3,"h":9}', '{"icon":"heat", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
	( 2, 'charging_station', 'Charging Station', '{"x":6,"y":1,"w":3,"h":9}', '{"icon":"charging-station", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 ),
	( 3, 'water_treatment', 'Water Treatment', '{"x":9,"y":1,"w":3,"h":9}', '{"icon":"water", "background":"linear-gradient(to bottom, #464646, #1a1a1a)"}', 'multi_knob', 1 );


INSERT INTO information_tile_measurement ( id, measurement_id, asset_category_id, domain, direction, measurement_type_id, sensor_name, measurement_name, information_tile_id, aggregation_function ) VALUES
( 1, 2050, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'sum' ),
( 2, 2051, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'sum' ),
( 3, 2052, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'sum' ),
( 4, 2053, NULL, NULL, NULL, NULL, NULL, NULL, 2, 'sum' ),
( 5, 2054, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'sum' );

--TODO: Missing maybe measurement details.
INSERT INTO public.measurement_details(id, key, value, measurement_id) VALUES 
	(1, 'cumulative', 'true', 2009),
	(2, 'cumulative', 'true', 2006),
	(3, 'cumulative', 'true', 2007),
	(4, 'cumulative', 'true', 2001),
	(5, 'cumulative', 'true', 2002),
	(6, 'cumulative', 'true', 2003),
	(7, 'cumulative', 'true', 2004),
	(8, 'cumulative', 'true', 2005),
	(9, 'cumulative', 'true', 2010);