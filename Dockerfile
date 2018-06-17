FROM gradle

EXPOSE 9999

ENV ATOMS_HOME=/opt/atoms
USER atoms

# Build everything we need
ADD src ${ATOMS_HOME}/src
ADD build.gradle ${ATOMS_HOME}/build.gradle
ENV GRADLE_USER_HOME=${ATOMS_HOME}
WORKDIR ${ATOMS_HOME}
RUN mkdir -p "${ATOMS_HOME}/.gradle" && \
    echo "org.gradle.daemon=false" >> \
        ${ATOMS_HOME}/.gradle/gradle.properties && \
    gradle build

# Set up the runtime environment
RUN mkdir -p ${ATOMS_HOME}/var
ADD etc/atoms.conf ${ATOMS_HOME}/etc/atoms.conf
ADD atoms ${ATOMS_HOME}/atoms
ADD build/libs/atoms.jar ${ATOMS_HOME}/lib/atoms.jar

# Run the service
CMD ${ATOMS_HOME}/atoms --jar "${ATOMS_HOME}/lib/atoms.jar"
