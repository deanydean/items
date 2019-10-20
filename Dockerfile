#
# Copyright 2016, 2019, Matt Dean
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
FROM gradle

EXPOSE 9999

ENV ITEMS_HOME=/opt/items

# Build everything we need
ADD src ${ITEMS_HOME}/src
ADD build.gradle ${ITEMS_HOME}/build.gradle
ENV GRADLE_USER_HOME=${ITEMS_HOME}
WORKDIR ${ITEMS_HOME}
RUN mkdir -p "${ITEMS_HOME}/.gradle" && \
    echo "org.gradle.daemon=false" >> \
        ${ITEMS_HOME}/.gradle/gradle.properties && \
    gradle build
RUN mkdir -p ${ITEMS_HOME}/lib && \
    cp build/libs/items.jar ${ITEMS_HOME}/lib/items.jar && \
    rm -rf build .gradle

# Set up the runtime environment
RUN mkdir -p ${ITEMS_HOME}/var
ADD etc/items.conf ${ITEMS_HOME}/etc/items.conf
ADD items ${ITEMS_HOME}/items

# Set the runtime user
RUN useradd --system --home ${ITEMS_HOME} items
RUN chown -R items ${ITEMS_HOME}

# Run the service
USER items
CMD ${ITEMS_HOME}/items --jar "${ITEMS_HOME}/lib/items.jar"
