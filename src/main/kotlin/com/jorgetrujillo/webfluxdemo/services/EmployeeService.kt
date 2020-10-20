package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.exceptions.EmployeeAlreadyExistsException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EmployeeService(
  val employeeRepository: EmployeeRepository
) {

  suspend fun save(id: String? = null, employee: Employee): Employee {
    if (id == null) {
      if (employeeRepository.findOneByEmployeeId(employee.employeeId!!).awaitFirstOrNull() != null) {
        throw EmployeeAlreadyExistsException(employee.employeeId)
      }
    } else {
      if (employeeRepository.findById(id).awaitFirstOrNull() == null) {
        throw ResourceDoesNotExistException(id, "id")
      }
      employee.id = id
    }

    return employeeRepository.save(employee).awaitSingle()
  }

  suspend fun list(pageable: Pageable): Page<Employee> {
    val page: Page<Employee> = PageImpl(
      employeeRepository.findAllEmployees(pageable).collectList().awaitFirst(),
      pageable,
      employeeRepository.count().awaitSingle()
    )

    return page
  }

  suspend fun getByEmployeeId(employeeId: String): Employee? {
    return employeeRepository.findOneByEmployeeId(employeeId).awaitFirstOrElse {
      throw ResourceDoesNotExistException(employeeId)
    }
  }

  suspend fun delete(id: String) {
    employeeRepository.deleteById(id)
  }
}
