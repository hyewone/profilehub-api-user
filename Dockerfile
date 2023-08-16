FROM openjdk:17

ADD ./build/libs/*SNAPSHOT.war app.war

ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=prod"]