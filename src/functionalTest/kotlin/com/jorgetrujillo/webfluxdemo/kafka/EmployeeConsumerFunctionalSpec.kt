package com.jorgetrujillo.webfluxdemo.kafka

import com.jorgetrujillo.webfluxdemo.TestBase
import com.jorgetrujillo.webfluxdemo.utils.TestUtils
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EmployeeConsumerFunctionalSpec : TestBase() {

  @Autowired
  lateinit var employeePublisher: EmployeePublisher

  @Autowired
  lateinit var employeeConsumer: EmployeeConsumer

  @BeforeEach
  fun setup() {
  }

  @AfterEach
  fun cleanup() {
    employeeConsumer.messagesReceived.clear()
  }

  @Test
  fun `consume message`() {

    // given:
    val employeeId = System.currentTimeMillis().toString()
    val testEmployee = TestUtils.getTestEmployee("Joe", employeeId)
    runBlocking { employeePublisher.sendMessage(testEmployee.employeeId!!, testEmployee) }

    // when:
    employeeConsumer.messagesReceived

    // then:
    (1..20).find {
      if (employeeConsumer.messagesReceived.find { it?.employeeId == testEmployee.employeeId } != null) {
        return@find true
      }
      Thread.sleep(100)
      return@find false
    }
    val publishedEmployee = employeeConsumer.messagesReceived.find { it?.employeeId == testEmployee.employeeId }
    publishedEmployee?.employeeName shouldBe testEmployee.employeeName
  }
}
