IteMS - Simple Microservice for Data Units
==========================================

A simple microservice built on top of Spark (sparkjava.com) for holding small
data units.

Build
-----
To build you will need Java 11 + Gradle and need to run:

$ gradle build

Run
---
To run the microservice without any persistence:

$ java -jar build/libs/items.jar

To run the microservice backed by a MapDB map:

$ java -jar build/libs/items.jar --mapdb --mapdb-file items.db --mapdb-map items

To run the microservice backed by a MongoDB collection:

$ java -jar build/libs/items.jar --mongodb --mongdb-host localhost \
    --mongodb-port 27017 --mongo-db items --mongodb-col test-collection

Use
---
To use the service, access the URL:

http://localhost:9999/items/

to view all data units.

To view an entry, use:

http://localhost:9999/items/[name]

where [name] is the data you want to access.

To add/remove/modify, you need to use POST/DELETE/PUT requests to:

http://localhost:9999/items/[type]/[name]

where [name] is the data unit you want to change and [type] is the type of the data unit.
