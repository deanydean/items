
DOCKER_IMAGE_NAME ?= deanydean/items
DOCKER_IMAGE_VERSION ?= 2.0.0

MAPDB_FILE ?= ./items.db
MONGODB_HOST ?= localhost
MONGODB_PORT ?= 27017

JAR_FILE = build/libs/items.jar

.PHONY: build docker-image run run-mapdb run-mongo

default: build

build: $(JAR_FILE)

clean:
	gradle clean

docker-image:
	docker build -t $(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_VERSION) .

run: $(JAR_FILE)
	./items-server

run-mongo: $(JAR_FILE)
	ITEMS_CONFIG_FILE="./etc/items-mongodb.conf" \
		./items-server
		
$(JAR_FILE):
	gradle build