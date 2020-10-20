package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.webfluxdemo.domain.Employee
import com.jorgetrujillo.webfluxdemo.exceptions.EmployeeAlreadyExistsException
import com.jorgetrujillo.webfluxdemo.exceptions.ResourceDoesNotExistException
import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class EmployeeServiceSpec extends Specification {

  EmployeeService employeeService

  void setup() {
    employeeService = new EmployeeService(Mock(EmployeeRepository))
  }

  void 'create an employee'() {

    given:
    Employee expected = new Employee('Joe', 'id1')

    when:
    Employee actual = employeeService.save(null, expected)

    then:
    1 * employeeService.employeeRepository.findOneByEmployeeId(expected.employeeId) >> null
    1 * employeeService.employeeRepository.save({ Employee employee ->
      assert employee.employeeId == expected.employeeId
      assert employee.name == expected.name
      return true
    }) >> expected
    0 * _

    actual.is(expected)
  }

  void 'create an employee fails if one already exists with same employee id'() {

    given:
    Employee expected = new Employee('Joe', 'id1')

    when:
    Employee actual = employeeService.save(null, expected)

    then:
    1 * employeeService.employeeRepository.findOneByEmployeeId(expected.employeeId) >> new Employee('name', 'id')
    0 * _

    thrown(EmployeeAlreadyExistsException)
  }

  void 'update an employee'() {

    given:
    String id = 'XX'
    Employee expected = new Employee('Joe', 'id1')

    when:
    Employee actual = employeeService.save(id, expected)

    then:
    1 * employeeService.employeeRepository.findById(id) >> Optional.of(expected)
    1 * employeeService.employeeRepository.save({ Employee employee ->
      assert employee.id == id
      assert employee.employeeId == expected.employeeId
      assert employee.name == expected.name
      return true
    }) >> expected
    0 * _

    actual.is(expected)
  }

  void 'update an employee fails if it does not exist'() {

    given:
    String id = 'XX'
    Employee expected = new Employee('Joe', 'id1')

    when:
    employeeService.save(id, expected)

    then:
    1 * employeeService.employeeRepository.findById(id) >> Optional.ofNullable(null)
    0 * _

    thrown(ResourceDoesNotExistException)
  }

  void 'list employees'() {

    given:
    Pageable pageable = PageRequest.of(0, 100)
    List<Employee> expected = [new Employee('name', 'id')]

    when:
    Page<Employee> employeePage = employeeService.list(pageable)

    then:
    1 * employeeService.employeeRepository.findAll(pageable) >> new PageImpl<Employee>(expected)
    0 * _

    employeePage.content == expected
  }

  void 'get employee by id'() {

    given:
    Employee expected = new Employee('name', 'id')
    String employeeId = 'id'

    when:
    Employee actual = employeeService.getByEmployeeId(employeeId)

    then:
    1 * employeeService.employeeRepository.findOneByEmployeeId(employeeId) >> expected
    0 * _

    actual.is(expected)
  }

  void 'delete employee by id'() {

    given:
    String id = 'id'

    when:
    employeeService.delete(id)

    then:
    1 * employeeService.employeeRepository.deleteById(id)
    0 * _
  }

}
