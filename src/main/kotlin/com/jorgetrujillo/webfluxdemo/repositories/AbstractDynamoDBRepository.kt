package com.jorgetrujillo.webfluxdemo.repositories

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper

abstract class AbstractDynamoDBRepository<T>(
  val mapper: DynamoDBMapper
) {

  open fun save(item: T): T {
    // Note that save is void but does autopopulate fields
    mapper.save<T>(item)
    return item
  }

  open fun saveAll(items: List<T>): List<T> {
    mapper.batchSave(items)
    return items
  }

  open fun delete(item: T) {
    mapper.delete(item)
  }

  abstract fun deleteAll()
}
