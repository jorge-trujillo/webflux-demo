package com.jorgetrujillo.webfluxdemo.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import java.time.Instant

class InstantToStringTypeConverter : DynamoDBTypeConverter<String, Instant> {

  override fun convert(instant: Instant): String {
    return instant.toString()
  }

  override fun unconvert(s: String): Instant {
    return Instant.parse(s)
  }
}
