FROM gradle:8.11.1-jdk23-alpine AS build

WORKDIR /app

COPY build/libs/testBankApi-0.0.1-SNAPSHOT.jar /app/testBankApi.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "testBankApi.jar"]