package com.jorgetrujillo.webfluxdemo.exceptions

class UnsupportedSortException(
  private val sortField: String
) : RuntimeException("Cannot sort by field $sortField")
