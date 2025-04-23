#Build stage
FROM maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
EXPOSE 8081


