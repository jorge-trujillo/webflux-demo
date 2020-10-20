package com.jorgetrujillo.webfluxdemo.controllers

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.PageInfo
import com.jorgetrujillo.webfluxdemo.services.EmployeeService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employees")
class EmployeeController(
  val service: EmployeeService
) {

  @GetMapping("/{id}")
  @ResponseBody
  suspend fun listEmployees(@PathVariable id: String): Employee? {

    return service.getByEmployeeId(id)
  }

  @GetMapping
  @ResponseBody
  suspend fun listEmployees(pageable : PageInfo): Page<Employee> {

    return service.list(pageable.toPageable())
  }

  @PostMapping
  @ResponseBody
  suspend fun createEmployee(@RequestBody employee: Employee): Employee {

    return service.save(employee = employee)
  }
}
