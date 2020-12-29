package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.TestBase
import com.jorgetrujillo.webfluxdemo.exceptions.UnsupportedSortException
import com.jorgetrujillo.webfluxdemo.utils.TestUtils.Companion.getTestEmployee
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.Instant

class EmployeeRepositoryFunctionalSpec : TestBase() {

  @Autowired
  lateinit var repository: EmployeeRepository

  @BeforeEach
  fun setup() {
    repository.deleteAll()
  }

  @Test
  fun `save new employee`() {

    // given:
    val employee = getTestEmployee("Joe Test", "e1")

    // when:
    val result = runBlocking { repository.save(employee) }

    // then:
    result.employeeId shouldBe "e1"
    runBlocking { repository.findById(employee.employeeId!!) } shouldNotBe null
  }

  @Test
  fun `delete employee`() {

    // given:
    val employee = repository.save(getTestEmployee("Joe Test", "e1"))

    // when:
    runBlocking { repository.delete(employee) }

    // then:
    runBlocking { repository.findById(employee.employeeId!!) } shouldBe null
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "null:e1,e2,e3,e4",
      "Joe Test:e1",
      "joe*:e1",
      "john*:e2,e3",
      "peter*:e3"
    ],
    delimiter = ':'
  )
  fun `list employees`(name: String, expectedIds: String) {

    // given:
    val query = if (name == "null") null else name

    listOf(
      getTestEmployee("Joe Test", "e1", Instant.parse("2020-01-01T00:00:00Z")),
      getTestEmployee("John Test", "e2", Instant.parse("2020-01-02T00:00:00Z")),
      getTestEmployee("Peter Johnson", "e3", Instant.parse("2020-03-01T00:00:00Z")),
      getTestEmployee("Mike Roberts", "e4", Instant.parse("2020-01-04T00:00:00Z"))
    ).forEach {
      repository.save(it)
      println("Saved ${it.employeeName} -> ${it.employeeId}")
    }

    // when:
    val results = runBlocking {
      repository.listByFields(
        query,
        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "employeeId"))
      )
    }

    // then:
    results.content.map { it.employeeId }.joinToString(separator = ",") shouldBe expectedIds
  }

  @Test
  fun `list employees throws an exception with an invalid sort`() {

    // given:
    listOf(
      getTestEmployee("Joe Test", "e1", Instant.parse("2020-01-01T00:00:00Z")),
      getTestEmployee("John Test", "e2", Instant.parse("2020-01-02T00:00:00Z")),
      getTestEmployee("Peter Johnson", "e3", Instant.parse("2020-03-01T00:00:00Z")),
      getTestEmployee("Mike Roberts", "e4", Instant.parse("2020-01-04T00:00:00Z"))
    ).forEach {
      repository.save(it)
      println("Saved ${it.employeeName} -> ${it.employeeId}")
    }

    // when:
    val exception = shouldThrow<UnsupportedSortException> {
      runBlocking {
        repository.listByFields(
          "Joe*",
          PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "bogusField"))
        )
      }
    }

    // then:
    exception.shouldBeTypeOf<UnsupportedSortException>()
  }
}
