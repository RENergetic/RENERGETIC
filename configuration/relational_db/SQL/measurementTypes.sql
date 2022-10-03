
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 1, 'power_w', 'Power [W]', 'W', 'W', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 2, 'energy', 'Energy[Wh]', 'Wh', 'Wh', 1.0, '#AAAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 3, 'heat_energy_gj', 'Energy [GJ]', 'Wh', 'GJ', 0.0000036, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 4, 'temperature', 'Temperature [°C]', '°C', '°C', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 5, 'percentage', 'Ratio [%]', '%', '%', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 6, 'power_kw', 'Power [kW]', 'W', 'kW', 1000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 7, 'power', 'Power [MW]', 'W', 'MW', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 8, 'energy_kwh', 'Energy [kWh]', 'Wh', 'kWh', 1000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 9, 'energy_mwh', 'Power [MWh]', 'Wh', 'MWh', 1000000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 10, 'voltage_v', 'Voltage [V]', 'V', 'V', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 11, 'voltage_kv', 'Voltage [kV]', 'V', 'kV', 1000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 12, 'co2eq_gperkwh', 'CO2eq [g/kWh]', 'g/kWh', 'g/kWh', 1.0, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 13, 'co2eq_kgperMwh', 'CO2eq [kg/kWh]', 'g/kWh', 'kg/MWh', 1000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 14, 'co2eq_tgpermwh', 'CO2eq [t/MWh]', 'g/kWh', 't/MWh', 1000, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 15, 'flow_m3perh', 'Flow [m3/h]', 'm3/h', 'm3/h', 1, '#4CAF50');
INSERT INTO measurement_type ( id, name, label, base_unit, unit, factor, color ) 
    VALUES( 16, 'flow_lperh', 'Flow [l/h]', 'm3/h', 'l/h', 0.001, '#4CAF50');

COMMIT;