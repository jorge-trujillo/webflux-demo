package com.jorgetrujillo.webfluxdemo.domain

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class PageCriteria(
  val page: Int,
  val size: Int,
  val sort: String?
) {

  fun toPageable(): Pageable {
    val sorts = if (sort != null) parseSorts(sort) else Sort.unsorted()
    return PageRequest.of(page, size, sorts)
  }

  private fun parseSorts(sort: String): Sort {
    val sorts = sort.split(",")
      .map { it.trim() }
      .map { sortToken ->
        val fieldAndOrder: List<String> = sortToken.split(".")
        val direction = if (fieldAndOrder.size > 1) Sort.Direction.fromString(fieldAndOrder[1]) else Sort.Direction.ASC
        Sort.Order(direction, fieldAndOrder.first())
      }

    return Sort.by(sorts)
  }
}
