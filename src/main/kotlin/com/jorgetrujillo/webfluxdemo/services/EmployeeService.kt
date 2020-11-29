package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.clients.SocialSecurityServiceClient
import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceConflictException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EmployeeService(
  val employeeRepository: EmployeeRepository,
  val ssnClient: SocialSecurityServiceClient
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
    val employees = employeeRepository.listByFields(name, pageable)
    employees.content.forEach {
      launch { it.socialSecurityNumber = ssnClient.getSocialSecurity(it.employeeId)?.socialSecurityNumber }
    }

    employees
  }

  suspend fun getById(id: String): Employee? = coroutineScope {
    val employee = employeeRepository.findById(id).orElse(null)
    employee?.let {
      it.socialSecurityNumber = ssnClient.getSocialSecurity(it.employeeId)?.socialSecurityNumber
    }

    employee
  }

  suspend fun delete(id: String) {
    employeeRepository.deleteById(id)
  }
}
