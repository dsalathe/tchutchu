# tchutchu

[![Build](https://github.com/dsalathe/tchutchu/actions/workflows/build.yml/badge.svg)](https://github.com/dsalathe/tchutchu/actions/workflows/build.yml)

This project was generated from [spring-boot-scala-example](https://github.com/jecklgamis/spring-boot-scala-example).

You can run the project locally using docker directly: `docker run -p 8080:8080 coachdave/tchutchu:latest`

Otherwise clone the repo and:

+ `make all` to build everything.
+ `java -jar target/tchutchu.jar` to run the server.
+ Go to [localhost:8080](http://localhost:8080)
+ `sbt test` to launch tests

The project is also publicly available on a self-hosted Raspberry pi [here](https://tchutchu.mooo.com).

The project is also deployed on an [azure](https://tchutchu.azurewebsites.net) free tier using an azure web service. There is a very long cold start.

The project is also deployed on Google Cloud Platform (GCP) [here](https://tinyurl.com/tchutchu) using google cloud run.
