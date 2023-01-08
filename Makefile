
DOCKER_IMAGE_NAME ?= deanydean/items
DOCKER_IMAGE_VERSION ?= 1.0.0

MAPDB_FILE ?= ./items.db
MONGODB_HOST ?= localhost
MONGODB_PORT ?= 27017

.PHONY: build docker-image run run-mapdb run-mongo

default: build

build: build/libs/items.jar

clean:
	gradle clean

docker-image:
	docker build -t $(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_VERSION) .

run: build/libs/items.jar
	java -jar ./build/libs/items.jar

run-mapdb: build/libs/items.jar
	java -jar build/libs/items.jar \
		--mapdb \
		--mapdb-file $(MAPDB_FILE) \
		--mapdb-map items

run-mongo: build/libs/items.jar
	java -jar build/libs/items.jar \
		--mongodb \
		--mongdb-host $(MONGODB_HOST) \
    	--mongodb-port $(MONGODB_PORT) \
		--mongo-db items \
		--mongodb-col items-collection

build/libs/items.jar:
	gradle build