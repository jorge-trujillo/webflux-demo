package com.jorgetrujillo.webfluxdemo.config

import com.jorgetrujillo.webfluxdemo.repositories.EmployeeRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = [EmployeeRepository::class])
@EnableMongoAuditing
class MongoDBConfig
