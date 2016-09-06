FROM java:8

# Install maven
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src/main /code/src/main
RUN ["mvn", "install"]

CMD ["java",  "-Xmx860m", "-XX:+UseCompressedOops", "-cp", "target/classes:target/dependency/*", "com.datadrivendota.parser.Worker"]
