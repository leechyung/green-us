package com.example.greening.repository

import com.example.greening.domain.item.Withdraw
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class WithdrawRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun save(withdraw: Withdraw) {
        if (withdraw.withdrawSeq == 0) {
            em.persist(withdraw)
        } else {
            em.merge(withdraw)
        }
    }

    fun delete(withdraw: Withdraw) {
        if (em.contains(withdraw)) {
            em.remove(withdraw)
        } else {
            em.remove(em.merge(withdraw))
        }
    }

    fun deleteById(id: Int) {
        val withdraw = findOne(id)
        if (withdraw != null) {
            delete(withdraw)
        }
    }

    fun findOne(id: Int): Withdraw? {
        return em.find(Withdraw::class.java, id)
    }

    fun findAll(): List<Withdraw> {
        return em.createQuery("select w from Withdraw w", Withdraw::class.java).resultList
    }

    fun findById(withdrawSeq: Int): Withdraw? {
        return em.createQuery("select w from Withdraw w where w.withdrawSeq = :withdrawSeq", Withdraw::class.java)
            .setParameter("withdrawSeq", withdrawSeq)
            .resultList
            .firstOrNull()
    }

    fun findByUser(userSeq: Int): List<Withdraw> {
        return em.createQuery("select w from Withdraw w where w.user.userSeq = :userSeq", Withdraw::class.java)
            .setParameter("userSeq", userSeq)
            .resultList
    }
}
