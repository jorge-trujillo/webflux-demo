package com.jorgetrujillo.webfluxdemo.controllers

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.domain.ListResponse
import com.jorgetrujillo.webfluxdemo.domain.PageCriteria
import com.jorgetrujillo.webfluxdemo.services.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employees")
class EmployeeController(
  val service: EmployeeService
) {

  @GetMapping("/{employeeId}")
  @ResponseBody
  suspend fun getEmployee(
    @PathVariable employeeId: String
  ): Employee? {

    return service.getById(employeeId)
  }

  @GetMapping
  @ResponseBody
  suspend fun listEmployees(
    @RequestParam name: String,
    pageable: PageCriteria
  ): ListResponse<Employee> {

    return ListResponse(service.list(name, pageable.toPageable()))
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  @ResponseBody
  suspend fun createEmployee(@RequestBody employeeUpdate: EmployeeUpdate): Employee {

    return service.save(null, employeeUpdate)
  }

  @PutMapping("/{employeeId}")
  @ResponseBody
  suspend fun updateEmployee(
    @PathVariable employeeId: String,
    @RequestBody employeeUpdate: EmployeeUpdate
  ): Employee {

    return service.save(employeeId, employeeUpdate)
  }

  @DeleteMapping("/{employeeId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ResponseBody
  suspend fun deleteEmployee(
    @PathVariable employeeId: String,
  ) {
    service.delete(employeeId)
  }
}
