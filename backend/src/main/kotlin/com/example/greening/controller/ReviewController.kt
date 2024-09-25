package com.example.greening.controller

import com.example.greening.domain.item.Greening
import com.example.greening.domain.item.Review
import com.example.greening.domain.item.User
import com.example.greening.service.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
class ReviewController(private val reviewService: ReviewService) {

    @GetMapping("/byId/{id}")
    fun getReviewById(@PathVariable id: Int): ResponseEntity<Review> {
        val review = reviewService.findOne(id)
        return if (review != null) {
            ResponseEntity.ok(review)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createReview(@RequestBody review: Review): ResponseEntity<Review> {
        reviewService.saveReview(review)
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    @PutMapping("/update/{reviewSeq}")
    fun updateReview(@PathVariable reviewSeq: Int, @RequestBody newReview: Review): ResponseEntity<Review> {
        return try {
            reviewService.updateReview(reviewSeq, newReview)
            ResponseEntity.ok(newReview)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/delete/{reviewSeq}")
    fun deleteReview(@PathVariable reviewSeq: Int) {
        reviewService.deleteReview(reviewSeq)
    }

    @GetMapping("/list")
    fun getAllReview(): ResponseEntity<List<Review>> {
        val review = reviewService.findReview()
        return ResponseEntity.ok(review)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getReviewByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Review>> {
        val review = reviewService.findByUserSeq(userSeq)
        return ResponseEntity.ok(review)
    }

    @GetMapping("/byReviewSeq/{reviewSeq}")
    fun getUserByReviewSeq(@PathVariable reviewSeq: Int): ResponseEntity<User> {
        val user = reviewService.findByReviewSeq(reviewSeq)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/greeningByReviewSeq/{reviewSeq}")
    fun getGreeningByReviewSeq(@PathVariable reviewSeq: Int): ResponseEntity<Greening> {
        val greening = reviewService.findGreeningByReviewSeq(reviewSeq)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/greeningbyUserSeq/{userSeq}")
    fun getMyReviewGreeningByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Greening>> {
        val greening = reviewService.findMyReviewGreeningByUserSeq(userSeq)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/byUserSeqAndGreening/{gSeq}/{userSeq}")
    fun getReviewByUserSeqAndgSeq(@PathVariable userSeq: Int, @PathVariable gSeq: Int): ResponseEntity<Review> {
        val review = reviewService.findByUserSeqAndgSeq(userSeq, gSeq)
        return ResponseEntity.ok(review)
    }

    @GetMapping("/bygSeq/{gSeq}")
    fun getReviewByGreeningSeq(@PathVariable gSeq: Int): ResponseEntity<List<Review>> {
        val review = reviewService.findByGreeningSeq(gSeq)
        return ResponseEntity.ok(review)
    }

}