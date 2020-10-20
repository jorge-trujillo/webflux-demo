package com.jorgetrujillo.webfluxdemo.exceptions

class EmployeeAlreadyExistsException(
  private val employeeId: String
) : RuntimeException("$employeeId already exists")
