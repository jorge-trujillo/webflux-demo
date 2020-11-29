package com.jorgetrujillo.webfluxdemo.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.net.InetAddress

@Configuration
class WebConfig {

  @Bean("hostName")
  fun getHostName(): String {
    return InetAddress.getLocalHost().hostName
  }

  @Bean("primaryMapper")
  @Primary
  fun defaultObjectMapper(): ObjectMapper {
    return ObjectMapper()
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(JavaTimeModule())
      .registerModule(KotlinModule())
  }

  // Necessary for Feign testing client
  @Bean
  fun httpMessageConverters(): HttpMessageConverters {
    return HttpMessageConverters(
      true,
      listOf(MappingJackson2HttpMessageConverter(defaultObjectMapper()))
    )
  }
}
