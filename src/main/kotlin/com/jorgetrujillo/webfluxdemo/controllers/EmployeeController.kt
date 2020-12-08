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

  @GetMapping("/{id}")
  @ResponseBody
  suspend fun getEmployee(
    @PathVariable id: String
  ): Employee? {

    return service.getById(id)
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
  @ResponseStatus(code = HttpStatus.OK)
  @ResponseBody
  suspend fun createEmployee(@RequestBody employeeUpdate: EmployeeUpdate): Employee {

    return service.save(null, employeeUpdate)
  }

  @PutMapping("/{id}")
  @ResponseBody
  suspend fun updateEmployee(
    @PathVariable id: String,
    @RequestBody employeeUpdate: EmployeeUpdate
  ): Employee {

    return service.save(id, employeeUpdate)
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @ResponseBody
  suspend fun deleteEmployee(
    @PathVariable id: String,
  ) {
    service.delete(id)
  }
}
