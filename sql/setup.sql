CREATE USER root WITH PASSWORD '12345';

CREATE DATABASE tasklist WITH OWNER=root;
GRANT ALL PRIVILEGES ON DATABASE tasklist TO root;