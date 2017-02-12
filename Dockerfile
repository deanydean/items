FROM openjdk

EXPOSE 9999

ENV ATOMS_HOME=/opt/atoms

RUN mkdir -p ${ATOMS_HOME}/var

ADD etc/atoms.conf ${ATOMS_HOME}/etc/atoms.conf
ADD atoms ${ATOMS_HOME}/.
ADD build/libs/atoms.jar ${ATOMS_HOME}/lib/atoms.jar

CMD ${ATOMS_HOME}/atoms --jar "${ATOMS_HOME}/lib/atoms.jar"
