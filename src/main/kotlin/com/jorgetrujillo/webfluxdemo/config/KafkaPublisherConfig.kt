package com.jorgetrujillo.webfluxdemo.config

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.HashMap

@Configuration
class KafkaPublisherConfig {

  @Autowired
  lateinit var kafkaConfig: KafkaConfig

  @Bean
  fun kafkaAdmin(): KafkaAdmin {
    val configs: MutableMap<String, Any?> = HashMap()
    configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapAddress

    // Security settings
    configs.putAll(kafkaConfig.getSecurityProperties())

    return KafkaAdmin(configs)
  }

  @Bean
  fun kafkaAdminClient(): AdminClient {
    return AdminClient.create(kafkaAdmin().configurationProperties)
  }

  @Bean
  fun producerFactory(): ProducerFactory<String, String>? {
    if (!kafkaConfig.publisher.enabled) {
      return null
    }

    val configProps: MutableMap<String, Any> = HashMap()
    configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapAddress
    configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

    // Security settings
    configProps.putAll(kafkaConfig.getSecurityProperties())

    return DefaultKafkaProducerFactory<String, String>(configProps)
  }

  @Bean
  fun kafkaTemplate(): KafkaTemplate<String, String>? {
    if (!kafkaConfig.publisher.enabled) {
      return null
    }

    return KafkaTemplate(producerFactory()!!)
  }
}
