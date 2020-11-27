package com.jorgetrujillo.webfluxdemo.testclients

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.domain.EmployeeUpdate
import com.jorgetrujillo.webfluxdemo.domain.ListResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
  name = "employee",
  path = "/employees",
  url = "\${feign.app.url}",
  decode404 = true
)
interface EmployeeClient {

  @GetMapping("/{id}")
  fun getEmployee(
    @PathVariable id: String
  ): ResponseEntity<Employee>

  @GetMapping
  fun listEmployees(
    @RequestParam name: String?,
    @RequestParam page: Int?,
    @RequestParam size: Int?,
    @RequestParam sort: String?,
  ): ResponseEntity<ListResponse<Employee>>

  @PostMapping
  fun createEmployee(@RequestBody employeeUpdate: EmployeeUpdate): ResponseEntity<Employee>

  @PutMapping("/{id}")
  fun updateEmployee(
    @PathVariable id: String,
    @RequestBody employeeUpdate: EmployeeUpdate
  ): ResponseEntity<Employee>

  @DeleteMapping("/{id}")
  fun deleteEmployee(
    @PathVariable id: String
  ): ResponseEntity<Unit>
}
