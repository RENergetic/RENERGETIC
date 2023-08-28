INSERT INTO uuid VALUES ('3');

INSERT INTO measurement ( id, name, label, measurement_type_id, domain, direction, sensor_name, sensor_id, asset_id, asset_category_id, uuid ) VALUES
( 2000, 'renewable', 'Renewable heat', 9, 'heat', 'out', 'heat_meter', NULL, 3, NULL,'3' ),
( 2001, 'non_renewable', 'No renewable heat', 9, 'heat', 'out', 'heat_meter', NULL, 3, NULL,'3' ),
( 2002, 'external', 'External heat', 9, 'heat', 'out', 'heat_meter', NULL, 3, NULL,'3' ),
( 2003, 'renewable', 'Renewable electricity', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL,'3' ),
( 2004, 'non_renewable', 'No renewable electricity', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL,'3' ),
( 2005, 'external', 'External electricity', 9, 'electricity', 'out', 'electricity_meter', NULL, 3, NULL,'3' ),
( 2006, 'None', 'Heat consumption', 9, 'heat', 'in', 'heat_meter', NULL, 3, NULL,'3' ),
( 2007, 'None', 'Electricity consumption', 9, 'electricity', 'in', 'electricity_meter', NULL, 3, NULL,'3' );

/* Abstract meters configuration */
INSERT INTO abstract_meter ( id, name, domain, formula, condition) VALUES
(1, 'LRS', 'heat', '[2000]', NULL),
(2, 'LNS', 'heat', '[2001]', NULL),
(3, 'ERS', 'heat', '0.3*([2006]-[2000])', NULL),
(4, 'ENS', 'heat', '(1-0.3)*([2006]-[2000])', NULL),
(5, 'LOSSES', 'heat', '0', NULL),
(6, 'LOAD', 'heat', '[2006]', NULL),
(7, 'EXCESS', 'heat', '0', NULL),
(8, 'STORAGE', 'heat', '0', NULL),
(9, 'RES', 'heat', '0', NULL),
(10, 'NONRES', 'heat', '0', NULL),
(11, 'LRS', 'electricity', '[2003]', NULL),
(12, 'LNS', 'electricity', '[2004]', NULL),
(13, 'ERS', 'electricity', '0.8*([2007]-[2003])', NULL),
(14, 'ENS', 'electricity', '(1-0.8)*([2007]-[2003])', NULL),
(15, 'LOSSES', 'electricity', '0', NULL),
(16, 'LOAD', 'electricity', '[2007]', NULL),
(17, 'EXCESS', 'electricity', '0', NULL),
(18, 'STORAGE', 'electricity', '0', NULL),
(19, 'RES', 'electricity', '0', NULL),
(21, 'NONRES', 'electricity', '0', NULL);

/* Set KPI constant */
INSERT INTO kpi_constants (id, alpha, beta, gamma, delta) VALUES
(1, 0.1, 0.4, 0.1, 0.7);