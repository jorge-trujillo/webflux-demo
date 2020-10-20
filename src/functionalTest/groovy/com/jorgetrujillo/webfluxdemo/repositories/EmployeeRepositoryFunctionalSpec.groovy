package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.graphqldemo.TestBase
import com.jorgetrujillo.graphqldemo.domain.Employee
import org.springframework.beans.factory.annotation.Autowired

class EmployeeRepositoryFunctionalSpec extends TestBase {

  @Autowired
  EmployeeRepository repository

  void 'create employee'() {

    given:
    Employee employee = new Employee('Joe', 'id1')

    when:
    Employee saved = repository.save(employee)

    then:
    Employee retrieved = repository.findById(saved.id).orElse(null)
    retrieved.name == employee.name
    retrieved.employeeId == employee.employeeId
    retrieved.created
  }
}
