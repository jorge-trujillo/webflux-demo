package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.exceptions.EmployeeAlreadyExistsException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepositoryStandard
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class EmployeeServiceStandard(
  val employeeRepository: EmployeeRepositoryStandard
) {

  fun save(id: String? = null, employee: Employee): Employee {
    if (id == null) {
      if (employeeRepository.findOneByEmployeeId(employee.employeeId!!) != null) {
        throw EmployeeAlreadyExistsException(employee.employeeId)
      }
    } else {
      if (employeeRepository.findById(id).orElse(null) == null) {
        throw ResourceDoesNotExistException(id, "id")
      }
      employee.id = id
    }

    return employeeRepository.save(employee)
  }

  fun list(pageable: Pageable): Page<Employee> {
    return employeeRepository.findAllEmployees(pageable)
  }

  fun getByEmployeeId(employeeId: String): Employee? {
    return employeeRepository.findOneByEmployeeId(employeeId)
  }

  fun delete(id: String) {
    employeeRepository.deleteById(id)
  }
}
