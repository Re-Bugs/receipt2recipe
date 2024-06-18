# Base image
FROM openjdk:17-jdk

# Set environment variable
ENV GOOGLE_APPLICATION_CREDENTIALS="/home/ubuntu/receipt2recipe/etc/ignore/receipt2recipe-417613-1ac8da3dff85.json"

# Copy the JAR file
COPY build/libs/*SNAPSHOT.jar /app.jar

# Run the application
ENTRYPOINT ["sh", "-c", "nohup java -jar /app.jar &"]
