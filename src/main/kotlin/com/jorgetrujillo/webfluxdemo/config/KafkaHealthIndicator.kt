package com.jorgetrujillo.webfluxdemo.config

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.DescribeTopicsOptions
import org.apache.kafka.clients.admin.TopicDescription
import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health.Builder
import org.springframework.stereotype.Component

@Component
class KafkaHealthIndicator(
  val adminClient: AdminClient,
  val kafkaConfig: KafkaConfig
) : AbstractHealthIndicator() {

  companion object {
    const val RESPONSE_TIMEOUT = 2000
  }

  override fun doHealthCheck(builder: Builder) {

    val topicNames =
      kafkaConfig.consumer.topics.values + kafkaConfig.publisher.topics.values
        .distinct()
    val topicConfig = getTopicInfo(topicNames, adminClient)
    if (topicConfig != null) {
      builder.up()
    } else {
      builder.down()
    }
  }

  private fun getTopicInfo(topicNames: List<String>, adminClient: AdminClient): Map<String, TopicDescription>? {
    val describeOptions = DescribeTopicsOptions().timeoutMs(RESPONSE_TIMEOUT)
    return adminClient.describeTopics(topicNames, describeOptions).all().get()
  }
}
