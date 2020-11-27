package com.jorgetrujillo.webfluxdemo.exceptions

import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class RestExceptionHandler {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @ExceptionHandler(ServerWebInputException::class)
  fun handleServerWebInputException(e: ServerWebInputException): ResponseEntity<ErrorResponse> {
    return generateResponse(HttpStatus.BAD_REQUEST, listOf(e.message), e, "Error in JSON body")
  }

  @ExceptionHandler(WebExchangeBindException::class)
  fun handleMethodArgumentNotValid(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
    val errorList = ex
      .bindingResult
      .fieldErrors
      .map { fieldError: FieldError -> fieldError.defaultMessage }

    return generateResponse(HttpStatus.BAD_REQUEST, errorList, ex, "Error validating arguments")
  }

  @ExceptionHandler(IllegalArgumentException::class)
  fun handleBadRequestException(e: Exception): ResponseEntity<ErrorResponse> {

    return generateResponse(HttpStatus.BAD_REQUEST, listOf(e.message), e)
  }

  @ExceptionHandler(ResourceDoesNotExistException::class)
  fun handleResourceNotFound(e: ResourceDoesNotExistException): ResponseEntity<ErrorResponse> {

    return generateResponse(HttpStatus.NOT_FOUND, listOf(e.message), e)
  }

  @ExceptionHandler
  fun handleUnexpectedException(e: Exception): ResponseEntity<ErrorResponse> {
    runBlocking { log.warn("Unexpected error ${e::class.simpleName} with message ${e.message}", e) }
    return generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, listOf(e.message), e)
  }

  private fun generateResponse(
    status: HttpStatus,
    errors: List<String?>,
    e: Exception,
    message: String? = null
  ): ResponseEntity<ErrorResponse> {
    val errorMessage = message ?: e.message ?: "Unexpected error"
    runBlocking { log.warn("Exception ${e::class.simpleName} due to error '$errorMessage'") }
    return ResponseEntity(
      ErrorResponse(errorMessage, errors.filterNotNull()),
      status
    )
  }
}
