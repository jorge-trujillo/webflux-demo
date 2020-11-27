package com.jorgetrujillo.webfluxdemo.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "employees")
data class Employee(
  @Id
  var id: String? = null,
  val name: String,
  val employeeId: String,
  @CreatedDate
  val created: Instant? = null,
  @LastModifiedDate
  val lastModified: Instant? = null
) {

  @Transient
  var socialSecurityNumber: String? = null

  constructor(name: String, employeeId: String) :
    this(null, name, employeeId)

  constructor(employeeUpdate: EmployeeUpdate) :
    this(null, employeeUpdate.name, employeeUpdate.employeeId, null)
}
