# Items - Simple Microservice for Data Units

A simple microservice built on top of Spark (sparkjava.com) for holding small
data units.

## Build
To build you will need Java 20 + Gradle 8 and need to run:

```
$ gradle build
```

## Run
To run the microservice without any persistence:

```
$ java -jar build/libs/items.jar --heap
```

To run the microservice backed by a MapDB map:

```
$ java -jar build/libs/items.jar --mapdb --mapdb-file items.db --mapdb-map items
```

To run the microservice backed by a MongoDB collection:

```
$ java -jar build/libs/items.jar --mongodb --mongdb-host localhost \
    --mongodb-port 27017 --mongo-db items --mongodb-col test-collection
```

## Use
To use the service, access the URL:

http://localhost:9999/items/

to view all data units.

To view an entry, use:

http://localhost:9999/items/[name]

where [name] is the data you want to access.

To add/remove/modify, you need to use POST/DELETE/PUT requests to:

http://localhost:9999/items/[type]/[name]

where [name] is the data unit you want to change and [type] is the type of the data unit.

## Using API Keys

To require an API key with each request, add your keys to a `keys.txt` file and use the `--require-keys` argument when starting the microservice. Once this is enabled, you will need to provide a `x-api-key` header with each request containing one of the keys in the `keys.txt` file.
