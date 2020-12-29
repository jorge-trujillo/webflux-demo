package com.jorgetrujillo.webfluxdemo.repositories

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException
import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.exceptions.UnsupportedSortException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class EmployeeRepository(
  val client: AmazonDynamoDB
) : AbstractDynamoDBRepository<Employee>(DynamoDBMapper(client)) {

  companion object {
    const val TABLE_NAME = "employees"
  }

  @PostConstruct
  fun init() {
    val dynamoDB = DynamoDB(client)
    try {
      val tableRequest: CreateTableRequest = mapper
        .generateCreateTableRequest(Employee::class.java)
        .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))

      val provisionedThroughput = ProvisionedThroughput(5L, 5L)
      tableRequest.globalSecondaryIndexes.forEach { v ->
        v.provisionedThroughput = provisionedThroughput
      }

      val table = DynamoDB(client).createTable(tableRequest)
      table.waitForActive()
    } catch (e: ResourceInUseException) {
      /* NOOP */
    }
  }

  suspend fun findById(id: String): Employee? {

    val eav = mapOf(":employeeId" to AttributeValue().withS(id))

    val queryExpression = DynamoDBQueryExpression<Employee>()
      .withKeyConditionExpression("employeeId = :employeeId")
      .withExpressionAttributeValues(eav)

    val queryResult = mapper.query(Employee::class.java, queryExpression)
    return queryResult.firstOrNull()
  }

  suspend fun listByFields(
    name: String?,
    pageable: Pageable
  ): Page<Employee> = withContext(Dispatchers.IO) {

    val criteria: MutableList<String> = mutableListOf()
    val eav: MutableMap<String, AttributeValue> = mutableMapOf()

    val results = if (name == null) {
      val scanExpression = DynamoDBScanExpression()
      val scanResult = mapper.scan(Employee::class.java, scanExpression)
      PageImpl(scanResult, pageable, scanResult.size.toLong())
    } else if (name.endsWith("*")) {
      criteria.add("employeeNameLower")
      eav[":employeeNameLower"] = AttributeValue().withS(name.removeSuffix("*").toLowerCase())

      val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("contains(employeeNameLower, :employeeNameLower)")
        .withExpressionAttributeValues(eav)

      val scanResult = mapper.scan(Employee::class.java, scanExpression)
      PageImpl(scanResult, pageable, scanResult.size.toLong())
    } else {

      criteria.add("employeeNameLower")
      eav[":employeeNameLower"] = AttributeValue().withS(name.toLowerCase())
      val queryExpression = DynamoDBQueryExpression<Employee>()
        .withIndexName(Employee.EMPLOYEE_NAME_INDEX)
        .withConsistentRead(false)
        .withKeyConditionExpression("employeeNameLower = :employeeNameLower")
        .withExpressionAttributeValues(eav)

      val queryResult = mapper.query(Employee::class.java, queryExpression)
      PageImpl(queryResult, pageable, queryResult.size.toLong())
    }

    // Return sorted results
    PageImpl(sortResults(results.content, pageable), pageable, results.totalElements)
  }

  private fun sortResults(results: List<Employee>, pageable: Pageable): List<Employee> {
    if (pageable.sort.isUnsorted) {
      return results
    }
    val sortedResults = when (pageable.sort.first().property) {
      "employeeName" -> results.sortedBy { it.employeeName }
      "employeeId" -> results.sortedBy { it.employeeId }
      "startDate" -> results.sortedBy { it.startDate }
      else -> throw UnsupportedSortException(pageable.sort.first().property)
    }

    return if (pageable.sort.first().isDescending) {
      sortedResults.reversed()
    } else {
      sortedResults
    }
  }

  override fun deleteAll() {
    val dynamoDB = DynamoDB(client)
    val table = dynamoDB.getTable(TABLE_NAME)
    table.delete()
    table.waitForDelete()

    init()
  }
}
