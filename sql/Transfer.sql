-- Create transfers database and connect to it.
CREATE USER transfers WITH PASSWORD 'changeme';
CREATE DATABASE transfers;
GRANT ALL PRIVILEGES ON DATABASE transfers TO transfers;
\connect transfers transfers;

-- Create tables to store transfers.
CREATE TABLE transfers
(
    account VARCHAR(14) NOT NULL PRIMARY KEY,
    auth    BIGINT      NOT NULL,
    play    BIGINT      NOT NULL,
    expiry  TIMESTAMP   NOT NULL
);

CREATE INDEX transfers_expiry ON transfers (expiry);