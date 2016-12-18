AtoMS - Simple Microservice for Data Units
==========================================

A simple microservice built on top of Spark (sparkjava.com) for holding small
data units.

Build
-----
To build you will need Java 8 + Gradle and need to run:

$ gradle build

Run
---
To run the microservice:

$ java -jar build/libs/atoms.jar

Use
---
To use the service, access the URL:

http://localhost:4567/atoms/[name]

where [name] is the data you want to access.

Use HTTP methods to define what action you want perform on the named data unit.
