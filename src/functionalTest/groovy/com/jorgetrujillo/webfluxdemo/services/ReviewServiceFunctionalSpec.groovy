package com.jorgetrujillo.webfluxdemo.services

import com.jorgetrujillo.graphqldemo.TestBase
import com.jorgetrujillo.graphqldemo.domain.Review
import com.jorgetrujillo.graphqldemo.domain.ReviewCriteria
import com.jorgetrujillo.graphqldemo.repositories.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class ReviewServiceFunctionalSpec extends TestBase {

  @Autowired
  ReviewService reviewService

  @Autowired
  ReviewRepository reviewRepository

  void setup() {
    reviewRepository.deleteAll()
  }

  void 'list reviews with criteria'() {

    given:
    PageRequest pageRequest = PageRequest.of(0, 100)
    ReviewCriteria reviewCriteria = new ReviewCriteria('e1')

    reviewRepository.saveAll([
        new Review('e1', 'Great job', 5),
        new Review('e2', 'Great job', 5),
        new Review('e3', 'Great job', 5),
        new Review('e4', 'Great job', 5),
    ])

    when:
    Page<Review> reviewPage = reviewService.list(reviewCriteria, pageRequest)

    then:
    reviewPage.content*.employeeId == ['e1']
  }
}
