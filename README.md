# Car parking
This project is made to manage car parking based on human driver, car and records

## Contents
- [Technology](#Technology)
- [Installation](#Installation)
- [Configuration](#Configuration)
- 
## Technology
- Java
- Maven
- Spring Framework
- Spring Boot
- Spring Data JPA
- Spring Security
- Lombok
- Log4j2
- Springdoc
- Swagger
- Mockito
- Junit
- Mysql

## Installation


## Configuration

## application.yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/car_parking?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true
      username: your_database_username
      password: your_database_password
  
    jpa:
      hibernate:
        ddl-auto: update
  
  server:
    port: 8080

### Dockerfile
  FROM openjdk:17-jdk-slim-buster
  WORKDIR /app
  COPY target/Questionnaire_project-0.0.1-SNAPSHOT.jar /app/Questionnaire_project.jar
  ENTRYPOINT ["java", "-jar", "Questionnaire_project.jar"]
  EXPOSE 8080

### docker-compose.yaml
  services:
    mysql:
      image: mysql:8.0
      container_name: mysql
      environment:
        MYSQL_ROOT_PASSWORD: your_database_password
        MYSQL_DATABASE: car_parking
      ports:
        - "3306:3306"
      volumes:
        - mysql-data:/var/lib/mysql
      networks:
        - impaq
  
    springboot-app:
      build: .
      container_name: questionnaire_project
      environment:
        USER_DB: your_database_username
        PASS_DB: your_database_password
        URL_DB: "jdbc:mysql://mysql:3306/car_parking?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true"
      ports:
        - "8080:8080"
      depends_on:
        - mysql
      networks:
        - impaq



  
  networks:
    impaq:
  
  volumes:
    mysql-data:

