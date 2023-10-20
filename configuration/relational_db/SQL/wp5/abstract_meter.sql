INSERT INTO uuid VALUES ('3');

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, sensor_id, asset_id, asset_category_id, uuid ) VALUES
( 4000, 'energy_consumed', 'Electricity consumption', 8, 'electricity', 'in', 'energy_meter', NULL, 1, NULL, '3' ), /*energy consumption in kwh of all assets in energy_meter*/
( 4001, 'energy_produced', 'PV generation', 8, 'electricity', 'out', 'pv', NULL, 1, NULL, '3' ), /*energy production kwh from all assets in pv measurement*/
( 4002, 'heat_generated', 'Heat consumption', 17, 'heat', 'in', 'heat_meter', NULL, 1, NULL, '3' ), /*energy consumption gjh from all assets in heat_meter measurement*/
( 4003, 'heat_generated', 'Heat supply Veolia', 17, 'heat', 'out', 'heat_meter', NULL, 1, NULL, '3' ), /*energy supply gjh from all assets in heat_meter measurement*/
( 4004, 'energy_consumed', 'Heatpump electricity consumption', 8, 'electricity', 'in', 'energy_meter', NULL, 1, NULL, '3' ), /*TODO: energy consumption gjh from heatpump*/
( 4012, 'lrs', 'Local renewable sources', 22, 'electricity', NULL, 'abstract_meter', NULL, 1, NULL, '3' ), /*LRS*/
( 4013, 'ers', 'External renewable sources', 22, 'electricity', NULL, 'abstract_meter', NULL, 1, NULL, '3' ), /*ERS*/
( 4014, 'res', 'Storage RES', 22, 'electricity', NULL, 'abstract_meter', NULL, 1, NULL, '3' ), /*Storage RES*/
( 4015, 'load', 'Load', 22, 'electricity', NULL, 'abstract_meter', NULL, 1, NULL, '3' ), /*Load*/
( 4016, 'losses', 'Losses', 22, 'electricity', NULL, 'abstract_meter', NULL, 1, NULL, '3' ), /*Losses*/
( 4017, 'load', 'Load', 22, 'heat', NULL, 'abstract_meter', NULL, 1, NULL, '3' ); /*Load*/

INSERT INTO tags(id, key, value) VALUES 
(4000, 'type_data', 'real'),
(4001, 'interpolation_method', 'none');
    
INSERT INTO measurement_tags(tag_id, measurement_id) VALUES 
(4000, 4000),
(4001, 4000),
(4000, 4001),
(4000, 4002),
(4000, 4003),
(4000, 4004);

INSERT INTO measurement_details(id, measurement_id, key, value) VALUES
    (4000, 4000, 'cumulative', 'true'),
    (4001, 4001, 'cumulative', 'true'),
    (4002, 4002, 'cumulative', 'true'),
    (4003, 4003, 'cumulative', 'true'),
    (4004, 4004, 'cumulative', 'true');

/* Abstract meters configuration */
INSERT INTO abstract_meter ( id, name, domain, formula, condition) VALUES
/* Electricity Abtract meters */
(1, 'LOSSES', 'electricity', '0', NULL),
(2, 'LOAD', 'electricity', '[4000]', NULL), /*data for electricity consumption from the pilot*/
(3, 'EXCESS', 'electricity', '[4001]-[4000]', '[4000]<[4001]'), /*Excess=PV-load*/
(4, 'STORAGE', 'electricity', '0', NULL), /*data from the pilot for the battery*/
(5, 'RES', 'electricity', '0', NULL),
(6, 'NONRES', 'electricity', '0', NULL),
(7, 'LRS', 'electricity', '[4001]', NULL), /*data from pilot for all PVs generation*/
(8, 'LNS', 'electricity', '0', NULL),
(9, 'ERS', 'electricity', '0.8*([4000]-[4001])', '[4000]>[4001]'), /*ERS=C(Load-PV)*/
(10, 'ENS', 'electricity', '(1-0.8)*([4000]-[4001])', '[4000]>[4001]'), /*ENS=(1-C)(Load-PV)*/
/* Heat Abtract meters */
(11, 'LOSSES', 'heat', '0', NULL), 
(12, 'LOAD', 'heat', '[4002]', NULL), /* Heat consumption */
(13, 'EXCESS', 'heat', '0', NULL),
(14, 'STORAGE', 'heat', '0', NULL),
(15, 'RES', 'heat', '0', NULL),
(16, 'NONRES', 'heat', '0', NULL),
(17, 'LRS', 'heat', '[4017]-([4004]*(([4012]+[4012]-[4014])/([4015]+[4016]))*([4017]/(5+([4015]-[4001])))', NULL), /*Load(heat) - HPel * Share_non_RES(el) * Load(heat) / (5 + Load(el) - PV)*/
(18, 'LNS', 'heat', '[4004]*(([4012]+[4012]-[4014])/([4015]+[4016]))*([4017]/(5+([4015]-[4001])))', NULL), /*HPel * Share_non_RES(el) * Load(heat) / (5 + Load(el) - PV)*/
(19, 'ERS', 'heat', '0', NULL),
(20, 'ENS', 'heat', '[4003]', NULL); /* Energy supply (Veolia) */

/* Set KPI constant */
INSERT INTO kpi_constants (id, alpha, beta, gamma, delta) VALUES
(1, 0.1, 0.4, 0.1, 0.7);