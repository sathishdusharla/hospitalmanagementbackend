# Use official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Use smaller JDK image to run app
FROM eclipse-temurin:21-jdk

RUN apt-get update \
	&& apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8081

ENTRYPOINT ["/app/entrypoint.sh"]