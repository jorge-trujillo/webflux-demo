package com.jorgetrujillo.webfluxdemo.clients

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class GenericHttpClient {

  val client: OkHttpClient = OkHttpClient().newBuilder().build()

  suspend fun get(url: String): GenericHttpResponse = withContext(Dispatchers.IO) {
    // Build request
    val request = Request.Builder()
      .url(url)
      .build()

    val call = client.newCall(request)

    // Get response and status
    val response = call.execute()
    val statusCode = HttpStatus.valueOf(response.code)
    val bodyAsString = response.body?.string()

    GenericHttpResponse(
      statusCode,
      bodyAsString
    )
  }
}
