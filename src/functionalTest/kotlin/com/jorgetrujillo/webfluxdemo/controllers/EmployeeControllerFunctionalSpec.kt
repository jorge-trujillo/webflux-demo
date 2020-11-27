package com.jorgetrujillo.webfluxdemo.controllers

import com.jorgetrujillo.webfluxdemo.TestBase
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import com.jorgetrujillo.webfluxdemo.testclients.EmployeeClient
import com.jorgetrujillo.webfluxdemo.utils.TestUtils
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.time.Duration
import java.time.Instant

class EmployeeControllerFunctionalSpec : TestBase() {

  @Autowired
  lateinit var repository: EmployeeRepository

  @Autowired
  lateinit var webClient: EmployeeClient

  @BeforeEach
  fun setup() {
    repository.deleteAll()
  }

  @AfterEach
  fun cleanup() {
    repository.deleteAll()
  }

  @Test
  fun `get an employee by id`() {

    // given:
    val existingEmployee = repository.save(
      TestUtils.getTestEmployee("Joe", "e1")
    )

    // when:
    val response = webClient.getEmployee(existingEmployee.id!!)

    // then:
    response.statusCode shouldBe HttpStatus.OK
    response.body?.id shouldBe existingEmployee.id
    response.body?.employeeId shouldBe existingEmployee.employeeId
    Duration.between(response.body?.created, Instant.now()).toMinutes() shouldBeLessThanOrEqual 1
  }

  @Test
  fun `list employees`() {

    // given:
    repository.saveAll(
      listOf(
        TestUtils.getTestEmployee("Andrew Test", "e1"),
        TestUtils.getTestEmployee("John Test", "e2"),
        TestUtils.getTestEmployee("Peter Johnson", "e3"),
        TestUtils.getTestEmployee("Mike Roberts", "e4")
      )
    )

    // when:
    val response = webClient.listEmployees("Test", 0, 10, "name.asc")

    // then:
    response.statusCode shouldBe HttpStatus.OK
    response.body?.totalResults shouldBe 2
    response.body?.results?.joinToString(separator = ",") { it.employeeId } shouldBe "e1,e2"
  }

  @Test
  fun `create employee`() {

    // given:
    val employeeUpdate = EmployeeUpdate("Joe", "e1")

    // when:
    val response = webClient.createEmployee(employeeUpdate)

    // then:
    response.statusCode shouldBe HttpStatus.CREATED
    response.body?.employeeId shouldBe employeeUpdate.employeeId

    // and: Employee was saved to repo
    val savedEmployee = repository.findById(response.body?.id!!).orElse(null)
    savedEmployee.employeeId shouldBe employeeUpdate.employeeId
  }

  @Test
  fun `update employee`() {

    // given:
    val existingEmployee = repository.save(
      TestUtils.getTestEmployee("Joe", "e1")
    )
    val employeeUpdate = EmployeeUpdate("Pete", "e1")

    // when:
    val response = webClient.updateEmployee(existingEmployee.id!!, employeeUpdate)

    // then:
    response.statusCode shouldBe HttpStatus.OK
    response.body?.employeeId shouldBe employeeUpdate.employeeId
    response.body?.name shouldBe employeeUpdate.name

    // and: Employee was saved to repo
    val savedEmployee = repository.findById(response.body?.id!!).orElse(null)
    savedEmployee.name shouldBe employeeUpdate.name
  }

  @Test
  fun `delete employee`() {

    // given:
    val existingEmployee = repository.save(
      TestUtils.getTestEmployee("Joe", "e1")
    )

    // when:
    val response = webClient.deleteEmployee(existingEmployee.id!!)

    // then:
    response.statusCode shouldBe HttpStatus.NO_CONTENT

    // and: Employee was deleted from repo
    repository.findById(existingEmployee.id!!).orElse(null) shouldBe null
  }
}
