package com.jorgetrujillo.webfluxdemo.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted
import java.time.Instant

// Big drawback here, DynamoDB requires an empty constructor:
// https://stackoverflow.com/questions/51073135/dynamodbmapper-load-cannot-instantiate-kotlin-data-class
@DynamoDBTable(tableName = "employees")
data class Employee(

  @DynamoDBHashKey
  @DynamoDBAutoGeneratedKey
  var employeeId: String? = null,

  var employeeName: String,

  @DynamoDBIndexRangeKey(globalSecondaryIndexName = EMPLOYEE_NAME_INDEX)
  @DynamoDBTypeConverted(converter = InstantToStringTypeConverter::class)
  @DynamoDBRangeKey
  var startDate: Instant,

  @DynamoDBTypeConverted(converter = InstantToStringTypeConverter::class)
  @DynamoDBAttribute
  var created: Instant? = null,

  @DynamoDBTypeConverted(converter = InstantToStringTypeConverter::class)
  @DynamoDBAttribute
  var lastModified: Instant? = null
) {

  companion object {
    const val EMPLOYEE_NAME_INDEX = "employeeNameIndex"
  }
  @DynamoDBIgnore
  var socialSecurityNumber: String? = null

  // Global indices essentially copy the data to a shadow table with a new partition key
  // There is eventual consistency here, do not use it for driving writes
  // WCU >= the WCU on main table
  // Case-insensitive field for searching in Dynamo
  @DynamoDBIndexHashKey(globalSecondaryIndexName = EMPLOYEE_NAME_INDEX)
  fun getEmployeeNameLower(): String {
    return employeeName.toLowerCase()
  }

  // Not used, but Dynamo requires a setter for every field
  fun setEmployeeNameLower(employeeNameLower: String) {
  }

  constructor() :
    this("NOT SET", "NOT SET", Instant.now())

  constructor(employeeId: String, name: String) :
    this(employeeId, name, Instant.now(), Instant.now(), Instant.now())

  constructor(employeeUpdate: EmployeeUpdate) :
    this(employeeUpdate.employeeId, employeeUpdate.employeeName, Instant.now())
}
