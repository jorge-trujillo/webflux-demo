package com.jorgetrujillo.webfluxdemo.exceptions

class ResourceConflictException(
  private val resourceId: String
) : RuntimeException("Resource $resourceId already exists")
