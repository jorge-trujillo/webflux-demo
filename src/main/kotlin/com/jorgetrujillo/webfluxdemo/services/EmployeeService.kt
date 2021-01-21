package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.clients.SocialSecurityServiceClient
import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceConflictException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.kafka.EmployeePublisher
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EmployeeService(
  val employeeRepository: EmployeeRepository,
  val ssnClient: SocialSecurityServiceClient,
  val employeePublisher: EmployeePublisher
) {

  suspend fun save(employeeId: String? = null, employeeUpdate: EmployeeUpdate): Employee = coroutineScope {

    val employeeToSave: Employee

    employeeToSave = if (employeeId == null) {
      if (employeeRepository.findById(employeeUpdate.employeeId) != null) {
        throw ResourceConflictException(employeeUpdate.employeeId)
      }
      Employee(employeeUpdate)
    } else {
      val existingEmployee = employeeRepository.findById(employeeId)
        ?: throw ResourceDoesNotExistException(employeeId, "employeeId")

      existingEmployee.copy(
        employeeId = employeeId,
        employeeName = employeeUpdate.employeeName
      )
    }

    val savedEmployee = employeeRepository.save(employeeToSave)
    employeePublisher.sendMessage(employeeToSave.employeeId!!, savedEmployee)
    savedEmployee
  }

  suspend fun list(name: String, pageable: Pageable): Page<Employee> = coroutineScope {
    val employees = employeeRepository.listByFields(name, pageable)
    employees.content.forEach {
      if (it.employeeId != null) {
        launch { it.socialSecurityNumber = ssnClient.getSocialSecurity(it.employeeId!!)?.socialSecurityNumber }
      }
    }

    employees
  }

  suspend fun getById(id: String): Employee? = coroutineScope {
    val employee = employeeRepository.findById(id)
    employee?.let {
      if (it.employeeId != null) {
        it.socialSecurityNumber = ssnClient.getSocialSecurity(it.employeeId!!)?.socialSecurityNumber
      }
    }

    employee
  }

  suspend fun delete(id: String) {
    val employee = employeeRepository.findById(id)
    employee?.let { employeeRepository.delete(employee) }
  }
}
