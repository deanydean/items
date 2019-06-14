#
# Copyright 2016, 2017 Matt Dean
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

ENV ATOMS_HOME=/opt/atoms

# Build everything we need
ADD src ${ATOMS_HOME}/src
ADD build.gradle ${ATOMS_HOME}/build.gradle
ENV GRADLE_USER_HOME=${ATOMS_HOME}
WORKDIR ${ATOMS_HOME}
RUN mkdir -p "${ATOMS_HOME}/.gradle" && \
    echo "org.gradle.daemon=false" >> \
        ${ATOMS_HOME}/.gradle/gradle.properties && \
    gradle build
RUN mkdir -p ${ATOMS_HOME}/lib && \
    cp build/libs/atoms.jar ${ATOMS_HOME}/lib/atoms.jar && \
    rm -rf build .gradle

# Set up the runtime environment
RUN mkdir -p ${ATOMS_HOME}/var
ADD etc/atoms.conf ${ATOMS_HOME}/etc/atoms.conf
ADD atoms ${ATOMS_HOME}/atoms

# Set the runtime user
RUN useradd --system --home ${ATOMS_HOME} atoms
RUN chown -R atoms ${ATOMS_HOME}

# Run the service
USER atoms
CMD ${ATOMS_HOME}/atoms --jar "${ATOMS_HOME}/lib/atoms.jar"
