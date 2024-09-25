package com.example.greening.service

import com.example.greening.domain.item.Payment
import com.example.greening.repository.PaymentRepository
import com.example.greening.repository.UserRepository
import okhttp3.internal.http2.Http2Reader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PaymentService(private val paymentRepository: PaymentRepository, private val userRepository: UserRepository) {

    @Transactional
    fun savePayment(payment: Payment) {
        val user = payment.user?.let { userRepository.findById(it.userSeq)}
        if (user != null) {
            payment.user = user
        }
        paymentRepository.save(payment)
    }


    @Transactional
    fun updatePayment(paymentSeq: Int, newPayment: Payment) {
        val existingPayment = paymentRepository.findOne(paymentSeq)
        if (existingPayment != null) { // 필드를 직접 업데이트
            existingPayment.paymentContent = newPayment.paymentContent ?: existingPayment.paymentContent
            existingPayment.paymentMethod = newPayment.paymentMethod ?: existingPayment.paymentMethod
            existingPayment.paymentDate = newPayment.paymentDate ?: existingPayment.paymentDate
            existingPayment.paymentMoney = newPayment.paymentMoney ?: existingPayment.paymentMoney

            paymentRepository.save(existingPayment)
        } else {
            throw IllegalStateException("결제가 존재하지 않습니다.")
        }
    }


    @Transactional
    fun deletePayment(paymentSeq: Int) {
        paymentRepository.deleteById(paymentSeq)
    }

    fun findPayment(): List<Payment> {
        return paymentRepository.findAll()
    }

    fun findOne(paymentSeq: Int): Payment? {
        return paymentRepository.findOne(paymentSeq)
    }

    fun findById(paymentSeq: Int): Payment? {
        return paymentRepository.findById(paymentSeq)
    }

    fun findByUserSeq(userSeq: Int): List<Payment> {
        return paymentRepository.findByUserSeq(userSeq)
    }

}