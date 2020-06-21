#
# Copyright 2016, 2020, Matt Dean
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Image that can build items
FROM gradle:jdk11 AS builder

# Build everything we need
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
