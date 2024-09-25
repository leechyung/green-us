package com.example.greening.repository

import com.example.greening.domain.item.Payment
import com.example.greening.domain.item.Prize
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PrizeRepository : JpaRepository<Prize, Int> {
    fun findByUser_UserSeq(userSeq: Int): List<Prize>

    @Query("select p from Prize p where p.participate.pSeq = :pSeq")
    fun findByParticipate_PSeq(pSeq: Int): Prize?

    @Query("select p from Prize p where p.user.userSeq = :userSeq and p.greening.gSeq = :gSeq")
    fun findByUserSeqAndGSeq(userSeq: Int, gSeq:Int): Prize?

}