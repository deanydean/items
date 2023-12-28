
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
	java -jar $< \
		--mapdb \
		--mapdb-file $(MAPDB_FILE) \
		--mapdb-map items

run-mongo: $(JAR_FILE)
	java -jar $< \
		--mongodb \
		--mongdb-host $(MONGODB_HOST) \
    	--mongodb-port $(MONGODB_PORT) \
		--mongo-db items \
		--mongodb-col items-collection

$(JAR_FILE):
	gradle build