package com.jorgetrujillo.webfluxdemo.controllers

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.services.EmployeeService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employees_coroutine")
class EmployeeCoRoutineController(
  val service: EmployeeService
) {

  @GetMapping
  @ResponseBody
  suspend fun listEmployees(@RequestParam page: Int = 0,
                            @RequestParam size: Int = 20): Page<Employee> {

    return service.list(PageRequest.of(page, size))
  }

  @PostMapping
  @ResponseBody
  suspend fun createEmployee(@RequestBody employee: Employee): Employee {

    return service.save(employee = employee)
  }
}
