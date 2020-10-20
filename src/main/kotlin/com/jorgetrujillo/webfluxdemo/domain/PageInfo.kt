package com.jorgetrujillo.webfluxdemo.domain

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

data class PageInfo(
  val page: Int,
  val size: Int,
  val sort: String
) {

  fun toPageable(): Pageable {
    return PageRequest.of(page, size)
  }
}
