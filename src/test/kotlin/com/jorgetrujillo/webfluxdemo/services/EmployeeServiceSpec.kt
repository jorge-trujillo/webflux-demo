package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceConflictException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import com.jorgetrujillo.webfluxdemo.utils.TestUtils.Companion.getTestEmployee
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional

internal class EmployeeServiceSpec {

  val service = EmployeeService(mockk<EmployeeRepository>())

  @BeforeEach
  fun setup() {
  }

  @Test
  fun `save a new employee`() {
    // given:
    val employeeUpdate = EmployeeUpdate(
      "Joe",
      "e1"
    )
    val expected = Employee(employeeUpdate)

    // Describe what mocks do when called

    // Call to get existing employee by ID
    coEvery {
      service.employeeRepository.findOneByEmployeeId("e1")
    } returns null
    // Save call to repo
    coEvery {
      service.employeeRepository.save(any<Employee>())
    } returns expected

    // when:
    val actual = runBlocking { service.save(null, employeeUpdate) }

    // then:
    coVerifyAll {
      service.employeeRepository.findOneByEmployeeId("e1")
      service.employeeRepository.save(
        withArg { employee: Employee ->
          employee.name shouldBe employeeUpdate.name
          employee.employeeId shouldBe employeeUpdate.employeeId
          employee.created shouldBe null
        }
      )
    }

    actual shouldBe expected
  }

  @Test
  fun `save a new employee fails if the employee id exists`() {
    // given:
    val employeeUpdate = EmployeeUpdate(
      "Joe",
      "e1"
    )
    val existingEmployee = getTestEmployee("Joe Existing", "e1")

    // Describe what mocks do when called
    // Call to get existing employee by ID
    coEvery {
      service.employeeRepository.findOneByEmployeeId("e1")
    } returns existingEmployee

    // when:
    val exception = shouldThrow<ResourceConflictException> {
      runBlocking { service.save(null, employeeUpdate) }
    }

    // then:
    coVerifyAll {
      service.employeeRepository.findOneByEmployeeId("e1")
    }

    exception.shouldBeTypeOf<ResourceConflictException>()
  }

  @Test
  fun `save an existing employee`() {
    // given:
    val existingId = "id1"
    val employeeUpdate = EmployeeUpdate(
      "Joe",
      "e1"
    )
    val existing = getTestEmployee(
      "Pete",
      "e1",
      true
    )
    val expected = getTestEmployee(
      "Joe",
      "e1"
    )

    // Describe what mocks do when called

    // Call to get existing employee by ID
    coEvery {
      service.employeeRepository.findById(existingId)
    } returns Optional.of(existing)
    // Save call to repo
    coEvery {
      service.employeeRepository.save(any<Employee>())
    } returns expected

    // when:
    val actual = runBlocking { service.save(existingId, employeeUpdate) }

    // then:
    coVerifyAll {
      service.employeeRepository.findById(existingId)
      service.employeeRepository.save(
        withArg { employee: Employee ->
          employee.name shouldBe employeeUpdate.name
          employee.employeeId shouldBe employeeUpdate.employeeId
          employee.created shouldNotBe null
        }
      )
    }

    actual shouldBe expected
  }

  @Test
  fun `list employees`() {
    // given:
    val name = "Joe"
    val expected = listOf(getTestEmployee("Joe", "e1"))
    val pageable = PageRequest.of(0, 20)

    // list call to repo
    coEvery {
      service.employeeRepository.listByFields(name, pageable)
    } returns PageImpl(expected)

    // when:
    val actual = runBlocking { service.list(name, pageable) }

    // then:
    coVerifyAll {
      service.employeeRepository.listByFields(name, pageable)
    }

    actual.content shouldBe expected
  }

  @Test
  fun `get one employee by id`() {
    // given:
    val employeeId = "e1"
    val expected = getTestEmployee("Joe", "e1")

    // list call to repo
    coEvery {
      service.employeeRepository.findOneByEmployeeId(employeeId)
    } returns expected

    // when:
    val actual = runBlocking { service.getByEmployeeId(employeeId) }

    // then:
    coVerifyAll {
      service.employeeRepository.findOneByEmployeeId(employeeId)
    }
    actual shouldBe expected
  }

  @Test
  fun `delete employee`() {
    // given:
    val id = "id1"

    // list call to repo
    coEvery {
      service.employeeRepository.deleteById(id)
    } just Runs

    // when:
    runBlocking { service.delete(id) }

    // then:
    coVerifyAll {
      service.employeeRepository.deleteById(id)
    }
  }
}
