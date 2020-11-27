package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.domain.Employee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.count
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class EmployeeRepositoryCustomImpl(
  val mongoTemplate: MongoTemplate
) : EmployeeRepositoryCustom {

  override suspend fun listByFields(name: String?, pageable: Pageable): Page<Employee> = withContext(Dispatchers.IO) {
    val criteria = Criteria()

    name?.let {
      criteria.and("name").regex(name, "i")
    }

    // Add paging
    val total = async { mongoTemplate.count<Employee>(Query.query(criteria)) }
    val productDetails = async {
      mongoTemplate.find<Employee>(Query.query(criteria).with(pageable))
    }

    PageImpl(productDetails.await(), pageable, total.await())
  }
}
