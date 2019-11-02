# Lambda

A bot for the JVMRally Discord server.

## Setup
* Create the following environment variables:
```
LAMBDA_TOKEN = YOUR_TOKEN
LAMBDA_DB_HOST = jdbc:dbtype://hostname:port/dbname
LAMBDA_DB_USER = dbuser
LAMBDA_DB_PASSWORD = dbpassword
LAMBDA_DB_DRIVER = org.postgresql.Driver
```

* Run main method normally for Flyway to begin building database structure
* To create/alter database structure create a new file in `resources/db/migration` formatted as `VX.X.X__Description_for_file.sql` (note the double underscore).

## Contributing

See [the contributing guide](contributing)