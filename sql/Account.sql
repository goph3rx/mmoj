-- Create accounts database and connect to it.
CREATE
USER accounts WITH PASSWORD 'changeme';
CREATE
DATABASE accounts;
GRANT ALL PRIVILEGES ON DATABASE
accounts TO accounts;
\connect accounts accounts;

-- Create tables to store account data.
CREATE TABLE accounts
(
    username     VARCHAR(14) NOT NULL PRIMARY KEY,
    salt         VARCHAR(32) NOT NULL,
    password     VARCHAR(64) NOT NULL,
    last_world   SMALLINT    NOT NULL,
    banned_until TIMESTAMP
);

