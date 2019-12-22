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

## Contributing

See [the contributing guide](CONTRIBUTING.md)