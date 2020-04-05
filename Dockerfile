FROM adoptopenjdk:12-jre-hotspot

COPY ./build/libs/a-maze-0.0.1-SNAPSHOT.jar backend.jar

COPY ./public ./public

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]
