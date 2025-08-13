# Usa un'immagine Maven con OpenJDK 17 come base per la fase di build
FROM maven:3-openjdk-17 AS build

# Imposta la directory di lavoro all'interno del container
WORKDIR /app

# Copia il file pom.xml per scaricare le dipendenze
# Questo passaggio è ottimizzato per sfruttare il caching di Docker
COPY pom.xml .

# Scarica tutte le dipendenze in modo che non debbano essere scaricate ogni volta
# Questo comando evita di riscaricare le dipendenze se il pom.xml non cambia
RUN mvn dependency:go-offline -B

# Copia il codice sorgente
COPY src ./src

# Esegui la fase di clean e package, saltando i test che richiedono il DB
# Questo risolve l'errore di connessione al database durante il build
RUN mvn clean install -DskipTests

# --- SOLUZIONE ALL'ERRORE: Usa l'immagine JRE supportata di Eclipse Temurin ---
# Usa un'immagine JRE più leggera per la fase di runtime finale
FROM eclipse-temurin:17-jre-focal

# Imposta la directory di lavoro per l'applicazione
WORKDIR /app

# Copia il file JAR generato dalla fase di build nella fase di runtime
# L'estrazione del nome del file JAR viene fatta automaticamente
COPY --from=build /app/target/*.jar app.jar

# Specifica il comando che verrà eseguito all'avvio del container
ENTRYPOINT ["java", "-jar", "app.jar"]
