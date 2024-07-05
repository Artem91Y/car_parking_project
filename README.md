# Car parking
This project is made to manage car parking based on human driver, car and records

## Contents
- [Technology](#Technology)
- [Installation](#Installation)
- [Configuration](#Configuration)
- [Useful endpoints](#Endpoints)
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
```bash
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
```

### Dockerfile
```bash
  FROM openjdk:17-jdk-slim-buster
  WORKDIR /app
  COPY target/newVersionOfCarParkingProject-0.0.1-SNAPSHOT.jar /app/CarParkingProject.jar
  ENTRYPOINT ["java", "-jar", "CarParkingProject.jar"]
  EXPOSE 8080
```

### docker-compose.yaml
```bash
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
      container_name: car_parking_project
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
```

## Endpoints

### For users

#### sigh in
 <font color="red"> url: /sighIn?your_role </font>
 
 request body: password, username
 
 response: true/false

 #### Create car
 <font color="red"> url: /saveCar </font>
 
 request body: number, type
 
 response: 201/500

 #### Create person
 <font color="red"> url: /savePerson </font>
 
 request body: numbers, fullName
 
 response: 201/500

 #### Buy parking place
 <font color="red"> url: /buyParkingPlace/{parking place number}?params </font>
 
 params: car number, startTime, endTime
 
 request body: none
 
 response: 200/500

 #### Delete booking record
  <font color="red"> url: /deleteBookingRecord </font>
  
 params: registration number(UUID format)

 request body: none
 
 response: 200/500/400

 #### Get car's booking records
 <font color="red"> url: /getCarsBookingRecord </font>
 
 params: registration number(UUID format)
 
 request body: none
 
 response: 200/500/400
 

 

