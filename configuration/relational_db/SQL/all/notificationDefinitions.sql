/* Check documentation to know the allowed types */

INSERT INTO notification_definition 
(id, code, message, type) VALUES
(1, 'RENEW', 'message.renewability', 'information'),
(2, 'A_HIGH', 'message.anomaly.high', 'warning'),
(3, 'A_LOW', 'message.anomaly.low', 'warning');

/* EXAMPLE NOTIFICATIONS */
INSERT INTO uuid VALUES ('3');

INSERT INTO dashboard
(id, name, label, url, uuid) VALUES
(1, 'notification_test', 'Test', 'http://grafana-ren-prototype.apps.paas-dev.psnc.pl/d/8P_WhuP7k/heat-demand?orgId=1&from=1669030768270&to=1671622768270&var-building=building1&theme=light', '3');

INSERT INTO notification_schedule
(id, date_from, date_to, notification_id, dashboard_id, asset_id, information_tile_id) VALUES
(1, now(), now() + interval '1 year', 2, 1, NULL, NULL),
(2, now() - interval '24 hour', now(), 1, NULL, NULL, NULL);