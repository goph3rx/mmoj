# mmoj

Suite of tools for creating MMO servers in Java.

## Dependencies

- `logback` for logging
- `dagger` for dependency injection
- `postgresql` and `jdbi3` for database connectivity
- `HikariCP` for connection pooling
- `junit` for unit testing
- `mockito` for mocking during testing

## Build

### Develop

- [Install GraalVM](https://www.graalvm.org/java/quickstart/)
- Build and run tests: `./mvnw package`

### Release

- [Install GraalVM](https://www.graalvm.org/java/quickstart/)
- Create a release: `./mvnw -P release package`:
    - `auth/target/auth-1.0-SNAPSHOT.jar` is an uber JAR for the auth server

## Install

### Servers

- [Install GraalVM](https://www.graalvm.org/java/quickstart/)
- Run the auth server: `java --enable-preview -jar auth-1.0-SNAPSHOT.jar`
    - This will load `auth.properties` configuration file

### Databases

- PostgreSQL is used to hold the data for the servers
- To create the necessary databases, execute each `.sql` file in the `sql`
  folder: `psql -U postgres -f sql/<database>.sql`
