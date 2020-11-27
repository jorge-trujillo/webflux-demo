package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.domain.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EmployeeRepositoryCustom {
  suspend fun listByFields(name: String?, pageable: Pageable): Page<Employee>
}
