package com.jorgetrujillo.webfluxdemo.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.jorgetrujillo.webfluxdemo.config.KafkaConfig
import com.jorgetrujillo.webfluxdemo.config.KafkaTopic
import com.jorgetrujillo.webfluxdemo.domain.Employee
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class EmployeePublisher(
  val kafkaTemplate: KafkaTemplate<String, String>?,
  @Qualifier("primaryMapper")
  val objectMapper: ObjectMapper,
  val kafkaConfig: KafkaConfig
) {

  val topicName = kafkaConfig.consumer.topics[KafkaTopic.EMPLOYEES.value]

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  suspend fun sendMessage(key: String, msg: Employee) = coroutineScope {
    if (kafkaTemplate == null) {
      log.warn("Kafka is disabled, so cannot publish messages")
      return@coroutineScope
    }

    val sendResult = kafkaTemplate!!.send(topicName!!, key, objectMapper.writeValueAsString(msg)).get()
    log.info(
      "Publishing message with key $key to topic $topicName on " +
        "${sendResult.recordMetadata.partition()}:${sendResult.recordMetadata.offset()}"
    )
  }
}
