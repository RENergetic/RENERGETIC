INSERT INTO uuid VALUES ('3');

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, sensor_id, asset_id, asset_category_id, uuid ) VALUES
( 4000, 'power', 'Electricity consumption', 1, 'electricity', 'in', 'energy_meter', NULL, 1000, NULL, '3' ), /*power W from all assets (de-nieuwe-dokken-private-***)*/
( 4001, 'energy', 'Battery storage', 2, 'electricity', 'in', 'energy_meter', NULL, 1114, NULL, '3' ), /*energy Wh from batteries, there aren't data on Influx*/
( 4002, 'power', 'PV generation', 1, 'electricity', 'out', 'pv', NULL, 1000, NULL, '3' ), /*power W from all assets in pv measurement*/
( 4003, 'power', 'Heat consumption', 1, 'heat', 'in', 'energy_meter', NULL, 1000, NULL, '3' ), /*power W from all assets (de-nieuwe-dokken-private-***)*/
( 4004, 'energy', 'Gas boiler heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1106, NULL, '3' ), /*energy Wh from gas boilers (de-nieuwe-dokken-gas-christyens)*/
( 4005, 'energy', 'Heatpump electricity consumption', 2, 'electricity', 'in', 'energy_meter', NULL, 1106, NULL, '3' ), /*energy Wh from Heat pump electricity consumption (de-nieuwe-dokken-heatpump)*/
( 4006, 'energy', 'Wasteheat heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1107, NULL, '3' ), /*energy Wh from Waste Christyens (de-nieuwe-dokken-wasteheat-christyens)*/
( 4007, 'energy', 'Christyens heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1108/*Is commented in pilot configuration*/, NULL, '3' ), /*energy Wh from Calorimeter Christyens Rest (de-nieuwe-dokken-total-heat-christyens)*/
( 4008, 'energy', 'Total heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1109, NULL, '3' ), /*energy Wh from Calorimeter Total (de-nieuwe-dokken-total-heat)*/
( 4009, 'energy', 'Heatpump heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1104, NULL, '3' ), /*energy Wh from Calorimeter Warmtepomp (de-nieuwe-dokken-heatpump)*/
( 4010, 'energy', 'Water treatment heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1105, NULL, '3' ), /*energy Wh from Calorimeter Waterzuivering (de-nieuwe-dokken-watertreatment) (Only there is electricity consumption data)*/
( 4011, 'energy', 'District heating heat generation', 2, 'heat', 'out', 'energy_meter', NULL, 1103, NULL, '3' ), /*energy Wh from Calorimeter Gebouwen (de-nieuwe-dokken-districtheating) (Only there is electricity consumption data)*/
( 4012, 'lrs', 'Local renewable sources', 22, 'electricity', NULL, 'abstract_meter', NULL, 1000, NULL, '3' ), /*LRS*/
( 4013, 'ers', 'External renewable sources', 22, 'electricity', NULL, 'abstract_meter', NULL, 1000, NULL, '3' ), /*ERS*/
( 4014, 'res', 'Storage RES', 22, 'electricity', NULL, 'abstract_meter', NULL, 1000, NULL, '3' ), /*Storage RES*/
( 4015, 'load', 'Load', 22, 'electricity', NULL, 'abstract_meter', NULL, 1000, NULL, '3' ), /*Load*/
( 4016, 'losses', 'Losses', 22, 'electricity', NULL, 'abstract_meter', NULL, 1000, NULL, '3' ); /*Losses*/

/* Abstract meters configuration */
INSERT INTO abstract_meter ( id, name, domain, formula, condition) VALUES
/* Electricity Abtract meters */
(1, 'LOSSES', 'electricity', '0', NULL),
(2, 'LOAD', 'electricity', '[4000]', NULL), /*data for electricity consumption from the pilot*/
(3, 'EXCESS', 'electricity', '[4002]-[4000]', '[4000] < [4002]'), /*Excess=PV-load*/
(4, 'STORAGE', 'electricity', '[4001]', NULL), /*data from the pilot for the battery*/
(5, 'RES', 'electricity', '0', NULL),
(6, 'NONRES', 'electricity', '0', NULL),
(7, 'LRS', 'electricity', '[4002]', NULL), /*data from pilot for all PVs generation*/
(8, 'LNS', 'electricity', '0', NULL),
(9, 'ERS', 'electricity', '0.8*([4000]-[4002])', '[4000] > [4002]'), /*ERS=C(Load-PV)*/
(10, 'ENS', 'electricity', '(1-0.8)*([4000]-[4002])', '[4000] > [4002]'), /*ENS=(1-C)(Load-PV)*/
/* Heat Abtract meters */
(11, 'LOSSES', 'heat', '([4004]+[4007]-[4008])+([4008]+[4009]-[4010]-[4011])', NULL), /*((calorimeter Christyens(gas) + calorimeter Christyens(rest)) - calorimeter total(scada)) + ((calorimeter total(scada) + calorimeter warmtepomp - calorimeter waterzuivering) - calorimeter gebouwen)*/
(12, 'LOAD', 'heat', '[4003]', NULL),
(13, 'EXCESS', 'heat', '0', NULL),
(14, 'STORAGE', 'heat', '0', NULL),
(15, 'RES', 'heat', '0', NULL),
(16, 'NONRES', 'heat', '0', NULL),
(17, 'LRS', 'heat', '[4005]*(1-(([4012]+[4012]-[4014])/([4015]+[4016]))*([4009]/[4005]))+[4006]', NULL), /*HPel * Share_RES(el) * COP + waste christyens*/ /*COP = calculated as ratio of heat supply of the heat pump to the electricity consumption of the heat pump*/
(18, 'LNS', 'heat', '[4005]*(([4012]+[4012]-[4014])/([4015]+[4016]))*([4009]/[4005])', NULL), /*HPel * Share_non_RES(el) * COP*/
(19, 'ERS', 'heat', '0', NULL),
(20, 'ENS', 'heat', '[4004]', NULL);

/* Set KPI constant */
INSERT INTO kpi_constants (id, alpha, beta, gamma, delta) VALUES
(1, 0.1, 0.4, 0.1, 0.7);