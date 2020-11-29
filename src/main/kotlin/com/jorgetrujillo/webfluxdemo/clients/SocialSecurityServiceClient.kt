package com.jorgetrujillo.webfluxdemo.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jorgetrujillo.webfluxdemo.clients.domain.SocialSecurityInfo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class SocialSecurityServiceClient(
  val genericHttpClient: GenericHttpClient,
  @Value("\${services.ssn.host}")
  val ssnHost: String,
  @Qualifier("primaryMapper")
  val objectMapper: ObjectMapper
) {

  companion object {
    const val SSN_SERVICE_PATH = "/social_security_numbers/"
  }

  suspend fun getSocialSecurity(employeeId: String): SocialSecurityInfo? {
    val url = ssnHost + SSN_SERVICE_PATH + employeeId
    val response = genericHttpClient.get(url)

    return if (response.statusCode == HttpStatus.OK && response.body != null) {
      objectMapper.readValue<SocialSecurityInfo>(response.body)
    } else {
      null
    }
  }
}
