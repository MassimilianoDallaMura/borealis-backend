# Utilizza un'immagine base con OpenJDK 17 e Maven
# Questa immagine include già il JDK e Maven, risolvendo il problema di 'mvn: command not found'
# Ho corretto il tag dell'immagine da '3.8.6-openjdk-17' a '3-openjdk-17' per risolvere l'errore 'not found'
FROM maven:3-openjdk-17

# Imposta la directory di lavoro all'interno del container
WORKDIR /app

# Copia il pom.xml e installa le dipendenze per velocizzare i rebuild successivi
# Questo passo evita di riscaricare le dipendenze ad ogni build, se non sono cambiate
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia il codice sorgente
COPY src ./src

# Esegui il comando di build di Maven per compilare il progetto e creare il file JAR
# -DskipTests salta i test unitari per velocizzare la build
RUN mvn clean install -DskipTests

# Espone la porta su cui l'applicazione Spring Boot sarà in ascolto
# La porta standard per le applicazioni Spring è 8080
EXPOSE 8080

# Questo è il comando che viene eseguito all'avvio del container
# Avvia l'applicazione usando il file JAR generato
ENTRYPOINT ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]