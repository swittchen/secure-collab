# -------------------------------------------------------------------
# STAGE 1: Build mit Maven
# -------------------------------------------------------------------
# Wir benutzen ein offizielles Maven-Image (inkl. JDK), um unser Jar zu bauen
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Arbeitsverzeichnis im Container
WORKDIR /app

# 1) Kopiere nur die pom.xml, um Dependencies zu cachen
COPY pom.xml .

# 2) Spring Boot Maven Plugin lädt via mvn clean package
#    -DskipTests überspringt die Tests beim Bauen
RUN mvn dependency:go-offline -B
# gehe offline und lade alle Dependencies, damit bei Quellcode-Änderungen
# der Cache nicht invalidiert wird

# 3) Kopiere den Quellcode
COPY src ./src

# 4) Baue das Jar
RUN mvn clean package -DskipTests

# -------------------------------------------------------------------
# STAGE 2: Runtime mit schlankem JDK
# -------------------------------------------------------------------
# Für den Betrieb genügt das reine JRE-Image
FROM eclipse-temurin:21-jdk

# Setze Umgebungsvariable, in der unser Jar landen wird
WORKDIR /app

# Kopiere das erzeugte Jar aus Stage 1
# Der Name muss mit dem Artifact-Id + Version in Deiner pom.xml übereinstimmen
COPY --from=builder /app/target/securecollab-0.0.1-SNAPSHOT.jar ./app.jar

# Exponiere Port 8080 (Standard von Spring Boot)
EXPOSE 8080

# Wenn jemand 'docker run' nutzt, wird dieser Befehl ausgeführt
ENTRYPOINT ["java", "-jar", "app.jar"]
