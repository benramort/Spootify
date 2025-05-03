#Build stage
FROM maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
RUN apt update && apt install -y doxygen
EXPOSE 8081


