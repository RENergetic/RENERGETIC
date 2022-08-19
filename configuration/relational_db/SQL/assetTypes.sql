
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 1, "room", "Room", "structural", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 2, "flat", "Flat", "structural", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 3, "building", "Building", "structural", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 4, "energy_island", "Energy island", "structural", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 5, "generation_plant", "Generation plant", "energy", 50 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 6, "heat_pump", "Heat pump", "energy", 40 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 7, "gas_boiler", "Gas boiler", "energy", 0 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 8, "co-generation_unit", "Co-generation unit", "energy", 0 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 9, "coal_plant", "Coal plant", "energy", 0 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 10, "pv_plant", "PV plant", "energy", 100 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 11, "external_heat_grid", "External heat grid", "energy", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 12, "external_electricity_grid", "External electricity grid", "energy", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 13, "solar_thermal_collector", "Solar thermal collector", "energy", 100 );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 14, "steam", "Steam", "infrastructure", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 15, "district_heating", "District heating", "infrastructure", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 16, "district_cooling", "District cooling", "infrastructure", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 17, "electricity", "Electricity", "infrastructure", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 18, "user", "User", "user", NULL );
INSERT INTO asset_type ( id, name, label, category, renovable ) 
    VALUES( 19, "ev_charging_station", "Ev charging station", "energy", 100 );

COMMIT;