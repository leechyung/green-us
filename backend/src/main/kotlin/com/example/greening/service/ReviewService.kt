package com.example.greening.service

import com.example.greening.domain.item.Greening
import com.example.greening.domain.item.Review
import com.example.greening.domain.item.User
import com.example.greening.repository.GreeningRepository
import com.example.greening.repository.ReviewRepository
import com.example.greening.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class ReviewService(private val reviewRepository: ReviewRepository, private val userRepository: UserRepository, private val greeningRepository: GreeningRepository) {

    @Transactional
    fun saveReview(review: Review) {
        val user = review.user?.let { userRepository.findById(it.userSeq)}
        val greening = review.greening?.let { greeningRepository.findById(it.gSeq)}
        if (user != null) {
            review.user = user
        }
        if (greening != null) {
            review.greening = greening
        }
        return reviewRepository.save(review)
    }

    @Transactional
    fun updateReview(reviewSeq: Int, newReview: Review) {
        val existingReview = reviewRepository.findOne(reviewSeq)
        if (existingReview != null) { // 필드를 직접 업데이트
            existingReview.user = newReview.user
            existingReview.greening = newReview.greening
            existingReview.reviewContent = newReview.reviewContent
            existingReview.reviewDate = newReview.reviewDate
            existingReview.reviewRate =  newReview.reviewRate

            reviewRepository.save(existingReview)
        } else {
            throw IllegalStateException("리뷰가 존재하지 않습니다.")
        }
    }


    @Transactional
    fun deleteReview(reviewSeq: Int) {
        reviewRepository.deleteById(reviewSeq)
    }

    fun findReview(): List<Review> {
        return reviewRepository.findAll()
    }

    fun findOne(reviewSeq: Int): Review? {
        return reviewRepository.findOne(reviewSeq)
    }

    fun findById(reviewSeq: Int): Review? {
        return reviewRepository.findById(reviewSeq)
    }

    fun findByGreeningSeq(gSeq: Int): List<Review> {
        return reviewRepository.findByGreening(gSeq)
    }

    fun findByUserSeq(userSeq: Int): List<Review> {
        return reviewRepository.findByUser(userSeq)
    }

    fun findByReviewSeq(reviewSeq: Int): User? {
        return reviewRepository.findByReview(reviewSeq)
    }

    fun findGreeningByReviewSeq(reviewSeq: Int): Greening? {
        return reviewRepository.findGreeningByReview(reviewSeq)
    }

    fun findByUserSeqAndgSeq(userSeq: Int, gSeq: Int): Review? {
        return reviewRepository.findByUserAndgSeq(userSeq, gSeq)
    }

    fun findMyReviewGreeningByUserSeq(userSeq: Int): List<Greening> {
        return reviewRepository.findMyReviewGreeningByUserSeq(userSeq)
    }
}