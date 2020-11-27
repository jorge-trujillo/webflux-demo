package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.domain.Employee
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : MongoRepository<Employee, String>, EmployeeRepositoryCustom {

  fun findOneByEmployeeId(employeeId: String): Employee?
}
