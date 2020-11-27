package com.jorgetrujillo.webfluxdemo

import org.junit.Test
import org.junit.runner.RunWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(
  classes = [DemoApplication::class],
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@EnableFeignClients
class TestBase {

  @Value("\${tests.mock_server.host}")
  lateinit var mockServerHost: String

  @Value("\${tests.mock_server.port:1080}")
  lateinit var mockServerPort: String

  companion object {
    var mockServerClient: ClientAndServer? = null
  }

  fun startMockServer() {
    val port = Integer.parseInt(mockServerPort)
    if (mockServerClient?.isRunning != true) {
      mockServerClient = startClientAndServer(port)
    }
    mockServerClient!!.reset()
  }

  fun getMockServerBase(): String {
    return "$mockServerHost:$mockServerPort"
  }

  // This is here to avoid error from JUnit about missing tests
  @Test
  fun base() {
  }
}
