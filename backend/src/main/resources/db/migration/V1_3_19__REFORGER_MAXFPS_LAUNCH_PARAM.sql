INSERT INTO launch_parameter (server_id, name, value)
SELECT id, 'maxFPS', '60'
FROM reforger_server
WHERE id NOT IN (SELECT server_id FROM launch_parameter WHERE name = 'maxFPS');
