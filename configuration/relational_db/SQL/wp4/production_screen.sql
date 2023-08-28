/*
1106, de-nieuwe-dokken-gas-christeyns, heat, fosil, supplier
1104, de-nieuwe-dokken-heatpump, heat, mixed, supplier
1107, de-nieuwe-dokken-wasteheat-christeyns, heat, mixed, supplier

1110, de-nieuwe-dokken-pv-017A-xxxxx9A1, electricity, renewable, supplier
1111, de-nieuwe-dokken-pv-0198-xxxxx4B4, electricity, renewable, supplier
1112, de-nieuwe-dokken-pv-0198-xxxxx853, electricity, renewable, supplier
1113, de-nieuwe-dokken-pv-0198-xxxxx9C0, electricity, renewable, supplier
*/

DELETE from public.information_tile_measurement WHERE id > 29999 AND id < 31000;
DELETE from public.measurement_details WHERE (id > 29999 AND id < 31000) OR key='color';
DELETE from public.measurement WHERE id > 29999 AND id < 31000;
DELETE from public.asset WHERE id > 29999 AND id < 31000;

UPDATE public.asset SET asset_category_id = NULL WHERE (id > 1109 AND id < 1114) OR id = 1104 OR id = 1107;
UPDATE public.asset SET asset_category_id = NULL WHERE id = 1106;

DELETE from public.asset_category WHERE id > 29999 AND id < 31000;
DELETE from public.information_tile WHERE id > 29999 AND id < 31000;
DELETE from public.information_panel WHERE id > 29999 AND id < 31000;

INSERT INTO public.asset(
	id, name, geo_location, label, asset_category_id, parent_asset_id, asset_type_id, user_id, uuid)
	VALUES 
    (30000, 'external', NULL, 'Energy Island external supplier', NULL, 1000, 5, NULL, 1);


INSERT INTO public.asset_category(
	id, description, label, name)
	VALUES 
    (30000, '', 'Renewable', 'renewable'),
	(30001, '', 'Fossil Fuels', 'fossil_fuels');

UPDATE public.asset SET asset_category_id = 30000 WHERE (id > 1109 AND id < 1114) OR id = 1104 OR id = 1107;
UPDATE public.asset SET asset_category_id = 30001 WHERE id = 1106;

INSERT INTO public.measurement(
	id, direction, domain, label, name, sensor_id, sensor_name, asset_id, asset_category_id, energy_island_asset_id, measurement_type_id, uuid)
	VALUES
    -- Electricity measurements
    (30000, 'out', 'electricity', 'Renewables', 'energy_wh', NULL, 'pv', NULL, 30000, 1000, 2, '1'),
	(30001, 'out', 'electricity', 'Fossil fuels', 'energy_wh', NULL, 'pv', NULL, 30001, 1000, 2, '1'),
	(30002, 'out', 'electricity', 'External', 'energy', NULL, 'energy_meter', 30000, NULL, 1000, 2, '1'),
    -- Heat measurements
    (30003, 'out', 'heat', 'Renewables', 'energy', NULL, 'energy_meter', NULL, 30000, 1000, 2, '1'),
	(30004, 'out', 'heat', 'Fossil fuels', 'energy', NULL, 'energy_meter', NULL, 30001, 1000, 2, '1'),
	(30005, 'out', 'heat', 'External', 'energy', NULL, 'energy_meter', 30000, NULL, 1000, 2, '1'),
    -- All measurements
    (30006, 'out', NULL, 'Renewables', 'energy', NULL, 'pv,energy_meter', NULL, 30000, 1000, 2, '1'),
	(30007, 'out', NULL, 'Fossil fuels', 'energy', NULL, 'pv,energy_meter', NULL, 30001, 1000, 2, '1'),
	(30008, 'out', NULL, 'External', 'energy', NULL, 'pv,energy_meter', 30000, NULL, 1000, 2, '1'),
    -- Total production
    (30009, 'out', NULL, 'Total production', 'percentage', NULL, 'energy_meter', NULL, NULL, 1000, 5, '1');

INSERT INTO measurement_details(
    id, measurement_id, key, value)
    VALUES
    (30100, 30000, 'cumulative', 'true'),
    (30101, 30001, 'cumulative', 'true'),
    (30103, 30003, 'cumulative', 'true'),
    (30104, 30004, 'cumulative', 'true'),
    (30106, 30006, 'cumulative', 'true'),
    (30107, 30007, 'cumulative', 'true');

INSERT INTO information_panel ( id, name, label, is_template, featured, uuid ) 
    VALUES
    ( 30001, 'production_screen_ren', 'RENergetic Energy Production', false, true, '1' );
    
INSERT INTO information_tile ( id, name, label, layout, props, type, information_panel_id ) 
    VALUES
    -- PRODUCTION SCREEN */
    ( 30001, 'energy_island_production', 'Energy Production', '{"x":0,"y":0,"w":12,"h":1}', '{"icon_visibility":false, "background":"none", "template":true}', 'single', 30001 ),
    ( 30002, 'energy_share', 'Energy share', '{"x":5,"y":1,"w":2,"h":1}', '{"icon_visibility":false, "background": "none"}', 'single', 30001 ),
    ( 30003, 'electricity', 'Electricity', '{"x":0,"y":1,"w":5,"h":10}', '{"icon":"electricity", "background":"linear-gradient(to right, #343638, #4ea5c5a0, #343638)"}', 'doughnut', 30001 ),
    ( 30004, 'heat', 'Heat', '{"x":7,"y":1,"w":5,"h":10}', '{"icon":"heat", "background":"linear-gradient(to right, #343638, #8e6faaa0, #343638)"}', 'doughnut', 30001 ),
    ( 30005, 'renewables', 'Renewables', '{"x":5,"y":2,"w":2,"h":3}', '{"icon":"renewables", "background":"radial-gradient #7ece9b80, #343638)"}','single', 30001 ),
    ( 30006, 'non_renewables', 'Fosil Fuels', '{"x":5,"y":4,"w":2,"h":3}', '{"icon":"fossil_fuels", "background":"radial-gradient(#c9595980, #343638)"}', 'single', 30001 ),
    ( 30007, 'external', 'External', '{"x":5,"y":6,"w":2,"h":3}', '{"icon":"import", "background":"radial-gradient(#d4bc6b80, #343638)"}', 'single', 30001 );


INSERT INTO information_tile_measurement ( id, measurement_id, asset_category_id, domain, direction, measurement_type_id, sensor_name, measurement_name, information_tile_id, aggregation_function ) 
    VALUES
    ( 30001, 30000, NULL, NULL, NULL, NULL, NULL, NULL, 30003, 'sum' ),
    ( 30002, 30001, NULL, NULL, NULL, NULL, NULL, NULL, 30003, 'sum' ),
    ( 30003, 30002, NULL, NULL, NULL, NULL, NULL, NULL, 30003, 'sum' ),
    ( 30004, 30003, NULL, NULL, NULL, NULL, NULL, NULL, 30004, 'sum' ),
    ( 30005, 30004, NULL, NULL, NULL, NULL, NULL, NULL, 30004, 'sum' ),
    ( 30006, 30005, NULL, NULL, NULL, NULL, NULL, NULL, 30004, 'sum' ),
    ( 30007, 30006, NULL, NULL, NULL, NULL, NULL, NULL, 30005, 'sum' ),
    ( 30008, 30007, NULL, NULL, NULL, NULL, NULL, NULL, 30006, 'sum' ),
    ( 30009, 30008, NULL, NULL, NULL, NULL, NULL, NULL, 30007, 'sum' ),
    ( 30010, 30009, NULL, NULL, NULL, NULL, NULL, NULL, 30001, 'mean' );

    
/* SET COLORS */
INSERT INTO public.measurement_details(
  id, key, value, measurement_id)
	SELECT  m.id, 'color', mt.color, m.id
	FROM measurement m JOIN measurement_type mt on m.measurement_type_id = mt.id;

UPDATE measurement_details
    SET value =
    CASE
    WHEN measurement.label = 'Renewables' THEN '#4ADE80'
    WHEN measurement.label = 'Fossil fuels' THEN '#E52122'
    WHEN measurement.label = 'External' THEN '#EAB403'
    WHEN measurement.label = 'Total production' THEN '9B59B6'
 END 
 FROM measurement WHERE  measurement.id = measurement_details.measurement_id 
AND measurement_details."key" = 'color';