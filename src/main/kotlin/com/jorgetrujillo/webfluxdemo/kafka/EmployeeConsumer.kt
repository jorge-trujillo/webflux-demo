package com.jorgetrujillo.webfluxdemo.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.jorgetrujillo.webfluxdemo.domain.Employee
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class EmployeeConsumer(
  @Qualifier("primaryMapper")
  val objectMapper: ObjectMapper
) {

  val messagesReceived: MutableList<Employee?> = Collections.synchronizedList(mutableListOf())

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val TOPIC_NAME = "test.employee-updates.v1"
  }

  @KafkaListener(topics = [TOPIC_NAME])
  fun listenWithHeaders(
    @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String,
    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: Int,
    @Header(KafkaHeaders.OFFSET) offset: Int,
    @Payload message: String?,
    acknowledgment: Acknowledgment
  ) {
    log.info("Received message for topic $TOPIC_NAME with key $key on partition $partition:$offset")

    // Try to deserialize
    val employee: Employee?
    try {
      employee = if (message != null) {
        objectMapper.readValue(message, Employee::class.java)
      } else {
        null
      }
    } catch (e: Exception) {
      log.error("Could not read message $key on topic $TOPIC_NAME on partition $partition:$offset", e)
      // There was an error deserializing here.
      // We should send this message to a dead letter queue or table and maybe send an alert
      return
    }

    // Process the message (add to database, send response, publish to different topic, etc)
    messagesReceived.add(employee)

    // Should only acknowledge if the message has been processed correctly! Otherwise should throw an
    // exception and let the retry handler deal with it
    acknowledgment.acknowledge()
  }
}
