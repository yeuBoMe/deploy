# FROM openjdk:21-jdk-slim
# WORKDIR /app
# COPY . .
# RUN apt-get update && apt-get install -y maven
# RUN mvn clean package -DskipTests
# RUN cp target/demo1-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
COPY ../uploads/ /app/uploads/  # Copy thư mục uploads từ cấp ngoài vào /app/uploads/
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests
RUN cp target/demo1-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]