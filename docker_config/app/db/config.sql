SELECT 
   'CREATE DATABASE keycloak
      WITH OWNER postgres 
      CONNECTION LIMIT  -1'
   WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak')\gexec

SELECT 
'CREATE DATABASE wso
   WITH OWNER postgres 
   CONNECTION LIMIT  -1'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'wso')\gexec