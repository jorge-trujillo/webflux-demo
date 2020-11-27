package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceConflictException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EmployeeService(
  val employeeRepository: EmployeeRepository
) {

  suspend fun save(id: String? = null, employeeUpdate: EmployeeUpdate): Employee = coroutineScope {

    val employeeToSave: Employee

    employeeToSave = if (id == null) {
      if (employeeRepository.findOneByEmployeeId(employeeUpdate.employeeId) != null) {
        throw ResourceConflictException(employeeUpdate.employeeId)
      }
      Employee(employeeUpdate)
    } else {
      val existingEmployee = employeeRepository.findById(id).orElse(null)
        ?: throw ResourceDoesNotExistException(id, "id")

      existingEmployee.copy(
        id = id,
        name = employeeUpdate.name,
        employeeId = employeeUpdate.employeeId
      )
    }

    employeeRepository.save(employeeToSave)
  }

  suspend fun list(name: String, pageable: Pageable): Page<Employee> = coroutineScope {
    employeeRepository.listByFields(name, pageable)
  }

  suspend fun getById(id: String): Employee? = coroutineScope {
    employeeRepository.findById(id).orElse(null)
  }

  suspend fun getByEmployeeId(employeeId: String): Employee? = coroutineScope {
    employeeRepository.findOneByEmployeeId(employeeId)
  }

  suspend fun delete(id: String) {
    employeeRepository.deleteById(id)
  }
}
