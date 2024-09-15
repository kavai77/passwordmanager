FROM eclipse-temurin:21
ADD target/passwordmanager-*.jar /usr/local/app.jar
ENTRYPOINT ["java", "-jar", "/usr/local/app.jar"]