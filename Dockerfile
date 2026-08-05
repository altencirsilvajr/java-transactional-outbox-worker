FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew gradlew
COPY gradle.properties settings.gradle build.gradle ./
COPY src src
RUN ./gradlew quarkusBuild -DskipTests --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /work
RUN useradd --system --uid 10001 --create-home quarkus
COPY --from=build --chown=quarkus:quarkus /workspace/build/quarkus-app/ ./
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "quarkus-run.jar"]
