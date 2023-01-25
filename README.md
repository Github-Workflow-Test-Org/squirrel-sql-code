# SQuirreL SQL Client

[SQuirreL SQL Client](http://www.squirrelsql.org/) is a graphical Java program
that will allow you to view the structure of a JDBC-compliant database, browse
the data in tables, issue SQL commands, etc.

Original Ant build instructions are described in the
[upstream](https://github.com/squirrel-sql-client/squirrel-sql-code)
repository.

---

This fork is to add [Gradle](https://gradle.org/) build scripts to the projects,
to take advantage of the incremental build feature of Gradle to produce fast builds
during development cycles.

## Gradle build instructions

There are two projects in the repository, in their own sub-directories:

* [sql12](sql12) is the project of the current SquirrelSQL application. It is based on the Swing framework for the user interface, and has been in use for a long time.
* [sqfx](sqfx) is the next-gen SquirrelSQLFX application which is based on JavaFX for the user interface. This project currently doesn't have as much functionalities as the other one.

The above two projects are independent. The Gradle build scripts for each project
(in their sub-directories) are therefore isolated and are independent builds.

In the main (parent) directory, there's the
[composite build](https://docs.gradle.org/current/userguide/composite_builds.html)
script which composes the two projects as included builds. In this main directory,
execute the `buildAll` task to build the two included projects:

	gradlew buildAll

The above composite `buildAll` task is equivalent to running the same task for
each independent project:

	gradlew :sql12:buildAll
	gradlew :sqfx:buildAll

Plain zip files are then produced in each project's `build` directory:

	sql12/build/plainZip/
	sqfx/build/plainZip/

For the `sql12` project, IzPack installer jars are also produced in its `build`
directory:

	sql12/build/izPackInstallJars/

Installation info for the above build artifacts are described
[here](http://www.squirrelsql.org/#installation).

Each project's application can be independently executed/run by its `runApp` task
using Gradle:

	gradlew :sql12:runApp

or:

	gradlew :sqfx:runApp


## Ant build instructions

Ant build instructions for the `sql12` project are in the upstream's
[README.md](https://github.com/squirrel-sql-client/squirrel-sql-code).

Ant build info for the `sqfx` project is described briefly
[here](http://www.squirrelsql.org/index.php?page=squirrelFx).

