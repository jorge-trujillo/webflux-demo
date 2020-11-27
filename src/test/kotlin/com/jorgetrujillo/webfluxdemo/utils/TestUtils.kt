package com.jorgetrujillo.webfluxdemo.utils

import com.jorgetrujillo.webfluxdemo.domain.Employee
import java.time.Instant

class TestUtils {

  companion object {
    fun getTestEmployee(
      name: String? = null,
      employeeId: String? = null,
      includeAuditing: Boolean = false
    ): Employee {
      return if (includeAuditing) {
        Employee(
          null,
          name ?: "Joe",
          employeeId ?: "e1",
          Instant.now(),
          Instant.now()
        )
      } else {
        Employee(
          name ?: "Joe",
          employeeId ?: "e1",
        )
      }
    }
  }
}
