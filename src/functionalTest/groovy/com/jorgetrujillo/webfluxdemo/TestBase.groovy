package com.jorgetrujillo.webfluxdemo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jorgetrujillo.graphqlclient.client.GraphQLClient
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Stepwise

import java.net.http.HttpClient

@SpringBootTest(
    classes = [DemoApplication],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = [DemoApplication])
@Stepwise
@Slf4j
class TestBase extends Specification {

  @Value('${mock_server.host}')
  String mockServerHost

  @Autowired
  MongoTemplate mongoTemplate

  @Autowired
  Environment environment

  static final int DEFAULT_PORT = 1080
  static String urlBase

  void setup() {
    String port = environment.getProperty('local.server.port')
    urlBase = "http://localhost:$port/graphql"

  }

}
