package com.jorgetrujillo.webfluxdemo.utils

import com.jorgetrujillo.webfluxdemo.domain.Employee
import java.time.Instant

class TestUtils {

  companion object {
    fun getTestEmployee(
      name: String? = null,
      employeeId: String? = null,
      startDate: Instant? = null,
      includeAuditing: Boolean = false
    ): Employee {
      return if (includeAuditing) {
        Employee(
          employeeId ?: "e1",
          name ?: "Joe",
          startDate ?: Instant.now(),
          Instant.now(),
          Instant.now()
        )
      } else {
        Employee(
          employeeId ?: "e1",
          name ?: "Joe"
        )
      }
    }
  }
}
