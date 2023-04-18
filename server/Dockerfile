FROM amazoncorretto:11
COPY target/*.jar shareit-server.jar
ENV JAVA_OPTS='-Xmx3g -Xms3g'
ENTRYPOINT ["java","-jar","/shareit-server.jar"]