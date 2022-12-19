-- Create worlds database and connect to it.
CREATE USER worlds WITH PASSWORD 'changeme';
CREATE DATABASE worlds;
GRANT ALL PRIVILEGES ON DATABASE worlds TO worlds;
\connect worlds worlds;

-- Create tables to store registered worlds.
CREATE TABLE worlds
(
    id              SMALLINT  NOT NULL PRIMARY KEY,
    ip              INET      NOT NULL,
    port            INTEGER   NOT NULL,
    current_players SMALLINT  NOT NULL,
    maximum_players SMALLINT  NOT NULL,
    is_online       BOOLEAN   NOT NULL,
    last_updated    TIMESTAMP NOT NULL
);

CREATE INDEX worlds_last_updated ON worlds (last_updated);
CREATE INDEX worlds_is_online ON worlds (is_online);
