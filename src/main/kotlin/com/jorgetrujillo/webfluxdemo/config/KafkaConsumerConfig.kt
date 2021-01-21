package com.jorgetrujillo.webfluxdemo.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
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

  @Autowired
  lateinit var kafkaConfig: KafkaConfig

  @Bean
  fun consumerFactory(): ConsumerFactory<String, String?>? {

    if (!kafkaConfig.consumer.enabled) {
      return null
    }

    val props: MutableMap<String, Any> = HashMap()
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapAddress
    props[ConsumerConfig.GROUP_ID_CONFIG] = kafkaConfig.consumer.groupId
    props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = kafkaConfig.consumer.autoOffsetReset
    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

    // Do not acknowledge automatically
    props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

    // Security settings
    props.putAll(kafkaConfig.getSecurityProperties())

    return DefaultKafkaConsumerFactory(props)
  }

  @Bean
  fun kafkaListenerContainerFactory(
    kafkaConsumerFactory: ConsumerFactory<String, String?>?
  ): ConcurrentKafkaListenerContainerFactory<String, String?>? {

    if (!kafkaConfig.consumer.enabled) {
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
