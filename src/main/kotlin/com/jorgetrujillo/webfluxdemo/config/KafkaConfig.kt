package com.jorgetrujillo.webfluxdemo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@ConfigurationProperties(prefix = "kafka")
class KafkaConfig {
  lateinit var bootstrapAddress: String
  var security: KafkaSecurity = KafkaSecurity()
  var publisher: KafkaPublisher = KafkaPublisher()
  var consumer: KafkaConsumer = KafkaConsumer()

  fun getSecurityProperties(): Map<String, String> {
    if (security.enabled) {
      val jaas =
        """
        org.apache.kafka.common.security.plain.PlainLoginModule   required
        username='${security.username}'
        password='${security.password}';
        """
      return mapOf(
        "sasl.mechanism" to "PLAIN",
        "sasl.jaas.config" to jaas,
        "security.protocol" to "SASL_SSL",
        "ssl.endpoint.identification.algorithm" to "",
        "ssl.truststore.location" to security.truststoreLocation!!,
        "ssl.truststore.password" to security.truststorePassword!!
      )
    }

    // If security is disabled, return empty map
    return mapOf()
  }
}

@Component
class KafkaPublisher {
  var enabled: Boolean = false
  var topics: Map<String, String> = mapOf()
}

@Component
class KafkaConsumer {
  var enabled: Boolean = false
  lateinit var groupId: String
  lateinit var autoOffsetReset: String
  var topics: Map<String, String> = mapOf()
}

@Component
class KafkaSecurity {
  var enabled: Boolean = false
  var username: String? = null
  var password: String? = null
  var truststoreLocation: String? = null
  var truststorePassword: String? = null
}

enum class KafkaTopic(
  val value: String
) {
  EMPLOYEES("employees")
}
