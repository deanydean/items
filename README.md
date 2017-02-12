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
To run the microservice without any persistence:

$ java -jar build/libs/atoms.jar

To run the microservice backed by a MapDB map:

$ java -jar build/libs/atoms.jar --mapdb --mapdb-file atoms.db --mapdb-map atoms

To run the microservice backed by a MongoDB collection:

$ java -jar build/libs/atoms.jar --mongodb --mongdb-host localhost \
    --mongodb-port 27017 --mongo-db atoms --mongodb-col test-collection

Use
---
To use the service, access the URL:

http://localhost:9999/atoms/[name]

where [name] is the data you want to access.

Use HTTP methods to define what action you want perform on the named data unit.
