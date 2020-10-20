package com.jorgetrujillo.webfluxdemo.resolvers

import com.jorgetrujillo.graphqlclient.domain.GraphQLField
import com.jorgetrujillo.graphqlclient.domain.GraphQLRequest
import com.jorgetrujillo.graphqlclient.domain.GraphQLRequestBuilder
import com.jorgetrujillo.graphqlclient.domain.GraphQLResponse
import com.jorgetrujillo.graphqldemo.TestBase
import com.jorgetrujillo.graphqldemo.domain.Employee
import com.jorgetrujillo.graphqldemo.domain.Review
import com.jorgetrujillo.graphqldemo.repositories.EmployeeRepository
import com.jorgetrujillo.graphqldemo.repositories.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired

class EmployeeQueryResolverFunctionalSpec extends TestBase {

  @Autowired
  EmployeeRepository employeeRepository

  @Autowired
  ReviewRepository reviewRepository

  @Autowired
  EmployeeQueryResolver employeeQueryResolver

  void setup() {
    employeeRepository.deleteAll()
  }

  void 'list employees with reviews'() {

    given:
    employeeRepository.save(new Employee(
        null,
        'Joe',
        'e1',
        null
    ))
    reviewRepository.save(new Review(
        null,
        'e1',
        'Great work',
        5,
        null
    ))
    GraphQLRequest request = new GraphQLRequestBuilder(
        'employees',
        GraphQLRequest.RequestType.QUERY)
        .addField(new GraphQLField('id', null))
        .addField(new GraphQLField('name', null))
        .addField(new GraphQLField('reviews',
            [
                new GraphQLField('reviewText', null),
                new GraphQLField('rating', null)
            ]))
        .build()

    when:
    GraphQLResponse<List<Employee>> response = graphQLClient.execute(request)
    List<Employee> employees = response.data['employees']

    then:
    response.statusCode == 200
    employees.size() == 1
    employees.first().id
    !employees.first().employeeId
    employees.first().name == 'Joe'
    employees.first().reviews.size() == 1
    employees.first().reviews.first().reviewText == 'Great work'
  }

  void 'list employees with reviews performance'() {

    given:
    List<Employee> employees = (0..10).collect {
      return new Employee(
          null,
          'Joe' + it,
          it as String,
          null
      )
    }
    employees.each { Employee employee ->
      employeeRepository.save(employee)
      (0..5).collect {
        return new Review(
            null,
            employee.employeeId,
            'Great work',
            5,
            null)
      }.each { reviewRepository.save(it) }
    }

    GraphQLRequest request = new GraphQLRequestBuilder(
        'employees',
        GraphQLRequest.RequestType.QUERY)
        .addField(new GraphQLField('id', null))
        .addField(new GraphQLField('name', null))
        .addField(new GraphQLField('reviews',
            [
                new GraphQLField('reviewText', null),
                new GraphQLField('rating', null)
            ]))
        .build()

    when:
    long totalTime = (1..100).sum {
      long requestStart = System.currentTimeMillis()
      GraphQLResponse<List<Employee>> response = graphQLClient.execute(request)
      long totalTime = System.currentTimeMillis() - requestStart
      println("Got response in ${totalTime} ms")
      return totalTime
    }
    println("Average time was ${totalTime / 100}")

    then:
    true
  }

}
