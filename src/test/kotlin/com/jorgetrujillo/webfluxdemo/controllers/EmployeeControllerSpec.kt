package com.jorgetrujillo.webfluxdemo.controllers

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.domain.PageCriteria
import com.jorgetrujillo.webfluxdemo.services.EmployeeService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

internal class EmployeeControllerSpec {

  val controller = EmployeeController(
    mockk<EmployeeService>()
  )

  @Test
  fun getEmployee() {
    // given:
    val id = "id1"
    val expected = Employee("Joe", "e1")

    coEvery {
      controller.service.getById(id)
    } returns expected

    // when:
    val actual = runBlocking { controller.getEmployee(id) }

    // then:
    coVerifyAll {
      controller.service.getById(id)
    }

    actual shouldBe expected
  }

  @Test
  fun listEmployees() {
    // given:
    val name = "joe"
    val pageCriteria = PageCriteria(0, 10, null)
    val pageable = PageRequest.of(0, 10)
    val expected = listOf(
      Employee("Joe", "e1")
    )

    coEvery {
      controller.service.list(name, pageable)
    } returns PageImpl(expected)

    // when:
    val actual = runBlocking { controller.listEmployees(name, pageCriteria) }

    // then:
    coVerifyAll {
      controller.service.list(name, pageable)
    }

    actual.results shouldBe expected
  }

  @Test
  fun createEmployee() {
    // given:
    val employeeUpdate = EmployeeUpdate("Joe", "e1")
    val expected = Employee("Joe", "e1")

    coEvery {
      controller.service.save(null, employeeUpdate)
    } returns expected

    // when:
    val actual = runBlocking { controller.createEmployee(employeeUpdate) }

    // then:
    coVerifyAll {
      controller.service.save(null, employeeUpdate)
    }

    actual shouldBe expected
  }

  @Test
  fun updateEmployee() {
    // given:
    val id = "id1"
    val employeeUpdate = EmployeeUpdate("Joe", "e1")
    val expected = Employee("Joe", "e1")

    coEvery {
      controller.service.save(id, employeeUpdate)
    } returns expected

    // when:
    val actual = runBlocking { controller.updateEmployee(id, employeeUpdate) }

    // then:
    coVerifyAll {
      controller.service.save(id, employeeUpdate)
    }

    actual shouldBe expected
  }
}
