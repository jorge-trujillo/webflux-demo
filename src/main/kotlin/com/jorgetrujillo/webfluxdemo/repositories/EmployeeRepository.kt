package com.jorgetrujillo.webfluxdemo.repositories

import com.jorgetrujillo.webfluxdemo.domain.Employee
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface EmployeeRepository : ReactiveMongoRepository<Employee, String> {

  @Query("{ id: { \$exists: true }}")
  fun findAllEmployees(page: Pageable?): Flux<Employee?>

  fun findOneByEmployeeId(employeeId: String): Mono<Employee?>
}
