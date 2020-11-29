# Kotlin Webflux Demo

This is a demo meant to demonstrate a Kotlin-based app that uses coroutines to drive a Spring webflux app. It also demonstrates how unit and functional tests can work on this type of app.

## Interesting libraries

- mockk: Mockk is an awesome mocking framework that, for Kotlin, is substantially superior to Mockito. This is because all classes are final by default in Kotlin.
- Kotest assertions: This assertions library makes code cleaner and easier to read. Still use Junit for the actual test framework, as I don't see the full testing framework bringing any value above the assertions library.
- openfeign: Library that makes calling and testing REST APIs a lot less onerous than with something like RestTemplate.

## Notable tests

- `EmployeeServiceSpec`: Unit test that shows how to use `mockk`, mocking both the database and the http client that calls an external service.
- `EmployeeRepositoryFuncionalSpec`: Functional test that uses a Dockerized Mongo instance to write data and read it back, ensuring your repository works.
- `EmployeeControllerFunctionalSpec`: Functional test that uses a Feign client, `EmployeeClient`, to call the controller methods and ensure the results are as expected. Note that the external API is not mocked, but instead is being served up by MockServer. This allows you to verify HTTP calls are actually being made and match your expectations.

## Running tests

Github will run your tests easily as an action as part of every PR. You should be running tests as often as possible!

## FAQ
- Why are you using non-reactive Mongo?
> Because the reactive mongo library has no facility for paging, as that is contrary to the reactive paradigm. However, you can still capture some of the value by calling Mongo inside a coroutine scope.

- Why the `PageCriteria` class?
> When you use webflux, the `Pageable` class does not seem to be able to be passed as an argument anymore on controller methods. Easiest way to get around this seemed to be to create a placeholder for it with conversion to `Pageable` built-in.
