package com.jorgetrujillo.webfluxdemo.exceptions

class ResourceDoesNotExistException(
  private val resourceId: String,
  private val fieldName: String? = null
) : RuntimeException("$resourceId does not exist") {

  constructor(employeeId: String) : this(employeeId, "employeeId")
}
