# Contributing

 - The project uses google style guide with the exception of using a tabsize of 4 spaces
[Intellij style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml), [Eclipse style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)

 - Prefix commit messages with feature/module. e.g. `[meta] Update readme`, or `[commands] Add new command`, or `[tasks] Add new task`

 - Use Windows style line endings (CRLF) when commiting. Git can automatically do that for you. See [core.autocrlf](https://git-scm.com/book/tr/v2/Customizing-Git-Git-Configuration).
 
 - Branches will be rebased into master branch. See https://trunkbaseddevelopment.com/
 
 - `com.jvmrally.lambda.db.*` contains auto-generated files from jooq.
 
 ## Commands
 
 Commands use the disparse library for command & argument parsing. For more information on how disparse works and how to integrate with it, see the [disparse readme.](https://github.com/BoscoJared/disparse)
 
 ### Basic Commands
 
 Commands that only require a MessageReceivedEvent can extend the [Command Class.](https://github.com/JVMRally/Lambda/blob/master/src/main/java/com/jvmrally/lambda/command/Command.java) This class' constructor takes a MessageReceivedEvent and has a variety of helper methods.
 
 ### Persistence Aware Commands 
 
 Commands that require access to the database can extend the [PersistenceAwareCommand Class.](https://github.com/JVMRally/Lambda/blob/master/src/main/java/com/jvmrally/lambda/command/PersistenceAwareCommand.java) 
 
 ### Audited Persistence Aware Commands 
 
 Commands that require access ot the database and additionally need to audit commands can extend the [AuditedPersistenceAwareCommand Class.](https://github.com/JVMRally/Lambda/blob/master/src/main/java/com/jvmrally/lambda/command/AuditedPersistenceAwareCommand.java)
 
 ## Tasks
 
 Tasks are pieces of code that can be repeated at a scheduled time. By default tasks are initially run on application startup. By implementing [DelayedTask](https://github.com/JVMRally/Lambda/blob/master/src/main/java/com/jvmrally/lambda/tasks/DelayedTask.java) you can override the `getTaskDelay` method to define the length of delay before a task should be first run.
 
 ## Setup
* Requires Java 11
* Requires maven
* Requires postgres database
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