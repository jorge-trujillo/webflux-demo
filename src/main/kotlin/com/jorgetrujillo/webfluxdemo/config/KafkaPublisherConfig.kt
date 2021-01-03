package com.jorgetrujillo.webfluxdemo.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.HashMap

@Configuration
class KafkaPublisherConfig {

  @Value("\${kafka.bootstrap_address}")
  lateinit var bootstrapAddress: String

  @Value("\${kafka.publisher.enabled}")
  val enabled: Boolean? = null

  @Bean
  fun kafkaAdmin(): KafkaAdmin {
    val configs: MutableMap<String, Any?> = HashMap()
    configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
    return KafkaAdmin(configs)
  }

  @Bean
  fun producerFactory(): ProducerFactory<String, String>? {
    if (enabled != true) {
      return null
    }

    val configProps: MutableMap<String, Any> = HashMap()
    configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
    configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    return DefaultKafkaProducerFactory<String, String>(configProps)
  }

  @Bean
  fun kafkaTemplate(): KafkaTemplate<String, String>? {
    if (enabled != true) {
      return null
    }

    return KafkaTemplate(producerFactory()!!)
  }
}
