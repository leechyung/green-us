package com.example.greening.repository

import com.example.greening.domain.item.Payment
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import okhttp3.internal.http2.Http2Reader
import org.springframework.stereotype.Repository

@Repository
class PaymentRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun save(payment: Payment) {
        if(payment.paymentSeq == 0) {
            em.persist(payment)
        }else{
            em.merge(payment)
        }
    }

    fun delete(payment: Payment) {
        if (em.contains(payment)) {
            em.remove(payment)
        } else {
            em.remove(em.merge(payment))
        }
    }

    fun deleteById(id: Int) {
        val payment = findOne(id)
        if (payment != null) {
            delete(payment)
        }
    }

    fun findOne(id : Int) : Payment?{
        return try{
            em.find(Payment::class.java, id)
        }catch(e: Exception){
            null
        }
    }

    fun findAll() : List<Payment>{
        return try{
            em.createQuery("select pay from Payment pay", Payment::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findById(paymentSeq: Int): Payment? {
        return try{
            em.createQuery("select pay from Payment pay where pay.paymentSeq = :paymentSeq", Payment::class.java)
                    .setParameter("paymentSeq", paymentSeq)
                    .singleResult
        }catch(e: IllegalStateException){
            null
        }
    }

    fun findByUserSeq(userSeq: Int): List<Payment> {
        return try{
            em.createQuery("select pay from Payment pay where pay.user.userSeq = :userSeq", Payment::class.java)
                    .setParameter("userSeq", userSeq)
                    .resultList
        }catch(e: Exception){
            emptyList()
        }
    }

}