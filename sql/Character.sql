-- Create the database to hold general character information and connect to it.
CREATE USER characters WITH PASSWORD 'changeme';
CREATE DATABASE characters;
GRANT ALL PRIVILEGES ON DATABASE characters TO characters;
\connect characters characters;

-- Create tables to store character info.
CREATE TABLE characters
(
    id           VARCHAR(41) NOT NULL PRIMARY KEY,
    name         VARCHAR(16) NOT NULL UNIQUE,
    account      VARCHAR(14) NOT NULL,
    race         INTEGER     NOT NULL,
    class        INTEGER     NOT NULL,
    gender       INTEGER     NOT NULL,
    appearance   VARCHAR(24) NOT NULL,
    created_on   TIMESTAMP   NOT NULL,
    delete_on    TIMESTAMP,
    last_used_on TIMESTAMP
);
CREATE INDEX characters_account ON characters (account);
CREATE INDEX characters_created_on ON characters (created_on);
CREATE INDEX characters_delete_on ON characters (delete_on);
