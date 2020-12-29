package com.jorgetrujillo.webfluxdemo.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamoConfig(
  @Value("\${amazon.dynamodb.endpoint}")
  private val amazonDynamoDBEndpoint: String,
  @Value("\${amazon.dynamodb.region}")
  private val amazonDynamoDBRegion: String,
  @Value("\${amazon.aws.accesskey}")
  private val amazonAWSAccessKey: String,
  @Value("\${amazon.aws.secretkey}")
  private val amazonAWSSecretKey: String
) {

  @Bean
  fun amazonDynamoDB(): AmazonDynamoDB? {
    val builder = AmazonDynamoDBClientBuilder
      .standard()
      .withCredentials(AWSStaticCredentialsProvider(amazonAWSCredentials()))

    if (!amazonDynamoDBEndpoint.isNullOrEmpty() && !amazonDynamoDBRegion.isNullOrEmpty()) {
      builder.setEndpointConfiguration(
        AwsClientBuilder.EndpointConfiguration(
          amazonDynamoDBEndpoint,
          amazonDynamoDBRegion
        )
      )
    }

    return builder.build()
  }

  @Bean
  fun amazonAWSCredentials(): AWSCredentials? {
    return BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey)
  }
}
