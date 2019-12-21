# Lambda

A bot for the JVMRally Discord server.

## Features

### Moderation

* Ban a user and delete their recent messages. The ban is logged in the database.
* Delete messages by user and channel.
* Send a direct message to a user via modmail.
* Mute a user.
* Alter the slowmode of a channel.
* Unmute a user.
* Warn a user. The warning is logged in the database and a direct message is sent to the user.

### Utility

* Create an embed message from json input.
* Create a giveaway.
* Generate a list of random winners for a giveaway.
* Search Java JEPs.
* Submit a suggestion for approval.
* Create, edit, delete, list, and display tags for easy access content.

### Misc

* Display github link about the bot.
* Display the server invite link.
* Display information on a user like server join date, register data.


### Automated tasks

* Listen to direct messages recieved from users and post them to a modmail channel.
* Check messages posted in all channels against spam filters.
* Post approved suggestions into a public channel for voting.
* Unban users after their ban time has expired.
* Unmute users after their mute time has expired.
* Scrape the openjdk website for new and updated JEPs.
* Create a new message in the weekly standup channel.

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

See [the contributing guide](CONTRIBUTING.md)