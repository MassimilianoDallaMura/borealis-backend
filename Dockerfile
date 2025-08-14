#====================
# STADIO 1: BUILD
# Questo stadio serve solo per compilare l'applicazione
# e creare il file JAR. Verrà scartato alla fine.
#====================
# Utilizziamo un'immagine Maven con JDK 17 per compilare il progetto.
FROM maven:3.8.3-openjdk-17 AS build

# Imposta la directory di lavoro all'interno del container
WORKDIR /app

# Copia l'intero progetto nella cartella di lavoro
COPY . .

# Esegui il build Maven. Ho aggiunto il flag '-DskipTests' come nel tuo screenshot.
RUN mvn clean install -DskipTests

#====================
# STADIO 2: PACKAGE
#====================
# Utilizziamo un'immagine più piccola basata su Eclipse Temurin con JDK 17.
FROM eclipse-temurin:17-jdk

# Copia il file JAR dallo stadio di build.
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar demo.jar

# Espone la porta su cui l'applicazione Spring Boot è in ascolto.
EXPOSE 8080

# Comando principale che verrà eseguito quando il container si avvia.
ENTRYPOINT ["java", "-jar", "demo.jar"]