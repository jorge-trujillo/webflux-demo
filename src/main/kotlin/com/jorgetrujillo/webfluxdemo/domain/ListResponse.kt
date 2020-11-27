package com.jorgetrujillo.webfluxdemo.domain

import org.springframework.data.domain.Page

data class ListResponse<T>(
  val results: List<T>,
  val totalResults: Long
) {
  constructor (page: Page<T>) : this(page.content, page.totalElements)
}
