package com.jorgetrujillo.webfluxdemo.clients

import org.springframework.http.HttpStatus

data class GenericHttpResponse(
  val statusCode: HttpStatus,
  val body: String?
)
