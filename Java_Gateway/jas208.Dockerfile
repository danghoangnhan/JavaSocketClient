FROM openjdk:11.0.4-jre-slim-buster
WORKDIR /

#enable the debugger port
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

ADD ./target/Java_JAS208_Gateway.jar /Java_JAS208_Gateway.jar
CMD ["java","-Xmx1G","-Xms1G","-jar", "Java_JAS208_Gateway.jar"]