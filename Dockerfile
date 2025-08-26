FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .

RUN apt-get update && apt-get install -y maven

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/library-0.0.1-SNAPSHOT.jar"]
