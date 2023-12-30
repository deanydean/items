# Image that can build items
FROM gradle:jdk21 AS builder
ADD src /home/gradle/src
ADD build.gradle /home/gradle/build.gradle
RUN mkdir -p .gradle && \
    echo "org.gradle.daemon=false" >> .gradle/gradle.properties && \
    gradle build

# The items service image
FROM amazoncorretto:21-alpine
ENV ITEMS_HOME=/opt/items
COPY --from=builder /home/gradle/build/libs/gradle.jar \
                    ${ITEMS_HOME}/build/libs/items.jar

# Set up the runtime environment
RUN mkdir -p ${ITEMS_HOME}/var
ADD etc/items.conf ${ITEMS_HOME}/etc/items.conf
ADD items-server ${ITEMS_HOME}/items-server

# Set the runtime user
RUN adduser -S -h ${ITEMS_HOME} items && \
    chown -R items ${ITEMS_HOME}

# Run the service
USER items
EXPOSE 9999
CMD sh ${ITEMS_HOME}/items-server