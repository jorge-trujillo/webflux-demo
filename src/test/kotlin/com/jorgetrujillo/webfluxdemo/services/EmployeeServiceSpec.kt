package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.clients.SocialSecurityServiceClient
import com.jorgetrujillo.webfluxdemo.clients.domain.SocialSecurityInfo
import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceConflictException
import com.jorgetrujillo.webfluxdemo.kafka.EmployeePublisher
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
import java.time.Instant

internal class EmployeeServiceSpec {

  val service = EmployeeService(
    mockk<EmployeeRepository>(),
    mockk<SocialSecurityServiceClient>(),
    mockk<EmployeePublisher>()
  )

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
      service.employeeRepository.findById("e1")
    } returns null
    // Save call to repo
    coEvery {
      service.employeeRepository.save(any<Employee>())
    } returns expected
    // Publish to topic
    coEvery {
      service.employeePublisher.sendMessage("e1", any<Employee>())
    } just Runs

    // when:
    val actual = runBlocking { service.save(null, employeeUpdate) }

    // then:
    coVerifyAll {
      service.employeeRepository.findById("e1")
      service.employeeRepository.save(
        withArg { employee: Employee ->
          employee.employeeName shouldBe employeeUpdate.employeeName
          employee.employeeId shouldBe employeeUpdate.employeeId
          employee.created shouldBe null
        }
      )
      service.employeePublisher.sendMessage("e1", any<Employee>())
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
      service.employeeRepository.findById("e1")
    } returns existingEmployee

    // when:
    val exception = shouldThrow<ResourceConflictException> {
      runBlocking { service.save(null, employeeUpdate) }
    }

    // then:
    coVerifyAll {
      service.employeeRepository.findById("e1")
    }

    exception.shouldBeTypeOf<ResourceConflictException>()
  }

  @Test
  fun `save an existing employee`() {
    // given:
    val existingId = "e1"
    val existing = getTestEmployee(
      "Pete",
      "e1",
      Instant.now(),
      true
    )
    val employeeUpdate = EmployeeUpdate(
      "Joe",
      "e1"
    )
    val expected = getTestEmployee(
      "Joe",
      "e1"
    )

    // Describe what mocks do when called

    // Call to get existing employee by ID
    coEvery {
      service.employeeRepository.findById(existingId)
    } returns existing
    // Save call to repo
    coEvery {
      service.employeeRepository.save(any<Employee>())
    } returns expected
    // Publish to topic
    coEvery {
      service.employeePublisher.sendMessage("e1", any<Employee>())
    } just Runs

    // when:
    val actual = runBlocking { service.save(existingId, employeeUpdate) }

    // then:
    coVerifyAll {
      service.employeeRepository.findById(existingId)
      service.employeeRepository.save(
        withArg { employee: Employee ->
          employee.employeeName shouldBe employeeUpdate.employeeName
          employee.employeeId shouldBe employeeUpdate.employeeId
          employee.created shouldNotBe null
        }
      )
      service.employeePublisher.sendMessage(
        "e1",
        withArg { employee: Employee ->
          employee.employeeName shouldBe employeeUpdate.employeeName
        }
      )
    }

    actual shouldBe expected
  }

  @Test
  fun `list employees`() {
    // given:
    val name = "Joe"
    val socialSecurityInfo = SocialSecurityInfo("1234", true)
    val expected = listOf(getTestEmployee("Joe", "e1"))
    val pageable = PageRequest.of(0, 20)

    // list call to repo
    coEvery {
      service.employeeRepository.listByFields(name, pageable)
    } returns PageImpl(expected)
    // Mocked call to external service
    coEvery {
      service.ssnClient.getSocialSecurity("e1")
    } returns socialSecurityInfo

    // when:
    val actual = runBlocking { service.list(name, pageable) }

    // then:
    coVerifyAll {
      service.employeeRepository.listByFields(name, pageable)
      service.ssnClient.getSocialSecurity("e1")
    }

    actual.content[0].employeeName shouldBe expected[0].employeeName
    actual.content[0].employeeId shouldBe expected[0].employeeId
    actual.content[0].socialSecurityNumber shouldBe socialSecurityInfo.socialSecurityNumber
  }

  @Test
  fun `get one employee by id`() {
    // given:
    val id = "id1"
    val expected = getTestEmployee("Joe", "e1")
    val socialSecurityInfo = SocialSecurityInfo("1234", true)

    // list call to repo
    coEvery {
      service.employeeRepository.findById(id)
    } returns expected
    coEvery {
      service.ssnClient.getSocialSecurity("e1")
    } returns socialSecurityInfo

    // when:
    val actual = runBlocking { service.getById(id) }

    // then:
    coVerifyAll {
      service.employeeRepository.findById(id)
      service.ssnClient.getSocialSecurity("e1")
    }

    actual?.employeeName shouldBe expected.employeeName
    actual?.employeeId shouldBe expected.employeeId
    actual?.socialSecurityNumber shouldBe socialSecurityInfo.socialSecurityNumber
  }

  @Test
  fun `delete employee`() {
    // given:
    val id = "id1"
    val expected = getTestEmployee("Joe", "e1")

    coEvery {
      service.employeeRepository.findById(id)
    } returns expected
    coEvery {
      service.employeeRepository.delete(expected)
    } just Runs

    // when:
    runBlocking { service.delete(id) }

    // then:
    coVerifyAll {
      service.employeeRepository.findById(id)
      service.employeeRepository.delete(expected)
    }
  }
}
