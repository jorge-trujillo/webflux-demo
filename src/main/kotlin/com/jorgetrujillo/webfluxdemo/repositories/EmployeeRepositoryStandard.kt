package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.domain.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface EmployeeRepositoryStandard : MongoRepository<Employee, String> {

  @Query("{ id: { \$exists: true }}")
  fun findAllEmployees(page: Pageable): Page<Employee>

  fun findOneByEmployeeId(employeeId: String): Employee?
}
