FROM openjdk:8u151-jre-alpine

COPY target/game-of-three-0.0.1-SNAPSHOT-spring-boot.jar game-of-three-server.jar

CMD ["/usr/bin/java", "-jar", "game-of-three-server.jar"]