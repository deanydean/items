# Image that can build items
FROM gradle:jdk11 AS builder
ADD src /home/gradle/src
ADD build.gradle /home/gradle/build.gradle
RUN mkdir -p .gradle && \
    echo "org.gradle.daemon=false" >> .gradle/gradle.properties && \
    gradle build

# The items service image
FROM openjdk:11-jre
ENV ITEMS_HOME=/opt/items
COPY --from=builder /home/gradle/build/libs/items.jar \
                    ${ITEMS_HOME}/lib/items.jar

# Set up the runtime environment
RUN mkdir -p ${ITEMS_HOME}/var
ADD etc/items.conf ${ITEMS_HOME}/etc/items.conf
ADD items ${ITEMS_HOME}/items

# Set the runtime user
RUN useradd --system --home ${ITEMS_HOME} items && \
    chown -R items ${ITEMS_HOME}

# Run the service
USER items
EXPOSE 9999
CMD ${ITEMS_HOME}/items --jar "${ITEMS_HOME}/lib/items.jar"