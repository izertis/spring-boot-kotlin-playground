# Customer JPA Example

**NOTE**: this project is part of a comparison between Java and Kotlin for an Adictos al Trabajo article. You can find the article [here](https://adictosaltrabajo.com/2025/11/11/TODO).

You can find the Java version of this project [here](../customer-address-jpa).

## Requirements

* JDK 21+
* Maven 3.8.+
* Docker Compose: in case you don't have Docker-Compose installed in your machine, install [Rancher Desktop](https://rancherdesktop.io/) and configure `dockerd` as engine (instead of `containerd`), this will include `docker` and `docker-compose` commands in your PATH.
* Your favorite IDE

## Getting Started

Use the following commands to run the application or tests:

* Start docker dependencies:

```bash
docker-compose up -d
```

* Run the application:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

* Open [Swagger UI](http://localhost:8080/swagger-ui/index.html) in your browser.
  Use "Basic Authentication" with username `user` and password `password` to authenticate.

* Running Unit Tests:

```bash
mvn clean test
```

* Running Unit and Integration Tests:

```bash
mvn clean verify
```

* Stop docker dependencies:

```bash
docker-compose down
```

