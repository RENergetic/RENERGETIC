SELECT setval('alert_threshold_id_seq', COALESCE((SELECT MAX(id)+1 FROM alert_threshold), 1), false);
SELECT setval('area_id_seq', COALESCE((SELECT MAX(id)+1 FROM area), 1), false);
SELECT setval('asset_category_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset_category), 1), false);
SELECT setval('asset_connection_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset_connection), 1), false);
SELECT setval('asset_details_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset_details), 1), false);
SELECT setval('asset_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset), 1), false);
SELECT setval('asset_rule_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset_rule), 1), false);
SELECT setval('asset_type_id_seq', COALESCE((SELECT MAX(id)+1 FROM asset_type), 1), false);
SELECT setval('dashboard_id_seq', COALESCE((SELECT MAX(id)+1 FROM dashboard), 1), false);
SELECT setval('hdr_recommendation_id_seq', COALESCE((SELECT MAX(id)+1 FROM hdr_recommendation), 1), false);
SELECT setval('heatmap_id_seq', COALESCE((SELECT MAX(id)+1 FROM heatmap), 1), false);
SELECT setval('information_panel_id_seq', COALESCE((SELECT MAX(id)+1 FROM information_panel), 1), false);
SELECT setval('information_tile_id_seq', COALESCE((SELECT MAX(id)+1 FROM information_tile), 1), false);
SELECT setval('information_tile_layout_id_seq', COALESCE((SELECT MAX(id)+1 FROM information_tile_layout), 1), false);
SELECT setval('information_tile_measurement_id_seq', COALESCE((SELECT MAX(id)+1 FROM information_tile_measurement), 1), false);
SELECT setval('measurement_details_id_seq', COALESCE((SELECT MAX(id)+1 FROM measurement_details), 1), false);
SELECT setval('measurement_id_seq', COALESCE((SELECT MAX(id)+1 FROM measurement), 1), false);
SELECT setval('measurement_type_id_seq', COALESCE((SELECT MAX(id)+1 FROM measurement_type), 1), false);
SELECT setval('notification_definition_id_seq', COALESCE((SELECT MAX(id)+1 FROM notification_definition), 1), false);
SELECT setval('notification_schedule_id_seq', COALESCE((SELECT MAX(id)+1 FROM notification_schedule), 1), false);
SELECT setval('tags_id_seq', COALESCE((SELECT MAX(id)+1 FROM tags), 1), false);
SELECT setval('user_demand_definition_id_seq', COALESCE((SELECT MAX(id)+1 FROM user_demand_definition), 1), false);
SELECT setval('user_demand_schedule_id_seq', COALESCE((SELECT MAX(id)+1 FROM user_demand_schedule), 1), false);
SELECT setval('user_roles_id_seq', COALESCE((SELECT MAX(id)+1 FROM user_roles), 1), false);
SELECT setval('user_settings_id_seq', COALESCE((SELECT MAX(id)+1 FROM user_settings), 1), false);
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id)+1 FROM users), 1), false);