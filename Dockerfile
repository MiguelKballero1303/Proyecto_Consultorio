FROM openjdk:21-slim

WORKDIR /app

COPY target/Proyecto_Calidad-0.0.1-SNAPSHOT.jar /app/Proyecto_Calidad.jar

EXPOSE 3000

CMD ["java", "-jar", "/app/Proyecto_Calidad.jar"]