#====================
# STADIO 1: BUILD
# Questo stadio serve solo per compilare l'applicazione
# e creare il file JAR. Verrà scartato alla fine.
#====================
# Utilizziamo un'immagine Maven con JDK 17 per compilare il progetto.
# 'AS build' serve a dare un nome a questo stadio.
FROM maven:3.8.3-openjdk-17 AS build

# Copia l'intero progetto nella cartella di lavoro all'interno del container.
COPY . .

# Esegui il build Maven per compilare il codice e creare il JAR.
# 'mvn clean install' pulisce, compila e installa l'applicazione nel repository locale.
RUN mvn clean install -DskipTests

#====================
# STADIO 2: PACKAGE
# Questo stadio crea l'immagine finale, che sarà molto più leggera
# perché contiene solo il runtime (JDK) e il file JAR.
#====================
# Utilizziamo un'immagine più piccola basata su Eclipse Temurin con JDK 17.
# Questo è un runtime ottimizzato per i container.
FROM eclipse-temurin:17-jdk

# Copia il file JAR compilato dallo 'stadio di build' (il primo stadio).
# La sintassi 'COPY --from=build' è fondamentale per il multi-stage build.
# Sostituisci 'familyhub-0.0.1-SNAPSHOT.jar' con il nome effettivo del tuo file JAR.
COPY --from=build /target/familyhub-0.0.1-SNAPSHOT.jar demo.jar

# Espone la porta su cui l'applicazione Spring Boot è in ascolto.
# La porta predefinita per Spring Boot è 8080.
EXPOSE 8080

# Comando principale che verrà eseguito quando il container si avvia.
# Esegue il file JAR utilizzando il comando 'java -jar'.
# Sostituisci 'demo.jar' se hai dato un nome diverso al file JAR.
ENTRYPOINT ["java", "-jar", "demo.jar"]