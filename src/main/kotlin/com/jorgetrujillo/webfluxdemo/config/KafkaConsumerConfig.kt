package com.jorgetrujillo.webfluxdemo.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.util.backoff.FixedBackOff
import java.util.HashMap

@EnableKafka
@Configuration
class KafkaConsumerConfig {

  @Value("\${kafka.bootstrap_address}")
  lateinit var bootstrapAddress: String

  @Value("\${kafka.consumer.enabled}")
  val enabled: Boolean? = null

  @Value("\${kafka.consumer.group_id}")
  lateinit var groupId: String

  @Value("\${kafka.consumer.auto_offset_reset}")
  lateinit var autoOffsetReset: String

  @Bean
  fun consumerFactory(): ConsumerFactory<String, String?>? {

    if (enabled != true) {
      return null
    }

    val props: MutableMap<String, Any> = HashMap()
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
    props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
    props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetReset

    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

    // Do not acknowledge automatically
    props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

    return DefaultKafkaConsumerFactory(props)
  }

  @Bean
  fun kafkaListenerContainerFactory(
    kafkaConsumerFactory: ConsumerFactory<String, String?>?
  ): ConcurrentKafkaListenerContainerFactory<String, String?>? {

    if (enabled != true) {
      return null
    }

    val factory = ConcurrentKafkaListenerContainerFactory<String, String?>()
    factory.consumerFactory = kafkaConsumerFactory

    // Do not ack automatically
    factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

    // Retry failed messages an unlimited number of times
    factory.setErrorHandler(
      SeekToCurrentErrorHandler(null, FixedBackOff(1000L, FixedBackOff.UNLIMITED_ATTEMPTS))
    )
    return factory
  }
}
