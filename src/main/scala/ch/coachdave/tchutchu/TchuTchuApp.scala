package ch.coachdave.tchutchu

import org.eclipse.jetty.server.{NetworkTrafficServerConnector, Server}
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Profile}

@SpringBootApplication
class TchuTchuApp

@main def main(): Unit = SpringApplication.run(classOf[TchuTchuApp])