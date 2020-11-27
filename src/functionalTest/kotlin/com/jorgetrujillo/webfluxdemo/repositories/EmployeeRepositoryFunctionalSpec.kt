package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.TestBase
import com.jorgetrujillo.webfluxdemo.utils.TestUtils.Companion.getTestEmployee
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class EmployeeRepositoryFunctionalSpec : TestBase() {

  @Autowired
  lateinit var repository: EmployeeRepository

  @BeforeEach
  fun setup() {
    repository.deleteAll()
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "null:e1,e2,e3,e4",
      "joe:e1",
      "john:e2,e3",
      "peter:e3"
    ],
    delimiter = ':'
  )
  fun `list employees`(name: String, expectedIds: String) {

    // given:
    val query = if (name == "null") null else name

    repository.saveAll(
      listOf(
        getTestEmployee("Joe Test", "e1"),
        getTestEmployee("John Test", "e2"),
        getTestEmployee("Peter Johnson", "e3"),
        getTestEmployee("Mike Roberts", "e4")
      )
    )

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
}
