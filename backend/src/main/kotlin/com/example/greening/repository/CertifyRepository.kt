package com.example.greening.repository

import com.example.greening.domain.item.Certify
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface CertifyRepository : JpaRepository<Certify, Int> {


    fun findByUser_UserSeq(userSeq: Int): List<Certify>

    fun findBypSeq(pSeq: Int): List<Certify>

    @Query("SELECT c FROM Certify c WHERE c.user.userSeq = :userSeq AND c.greening.gSeq = :gSeq")
    fun findByUserSeqAndGSeq(
        @Param("userSeq")userSeq: Int,
        @Param("gSeq") gSeq: Int
    ): List<Certify>

    @Query("SELECT c FROM Certify c WHERE c.user.userEmail = :userEmail AND c.greening.gSeq = :gSeq")
    fun findByUserEmailAndGSeq(
            @Param("userEmail") userEmail: String,
            @Param("gSeq") gSeq: Int
    ): List<Certify>

    @Query("SELECT c FROM Certify c WHERE c.user.userSeq = :userSeq AND c.greening.gSeq = :gSeq AND c.certifyDate = :certifyDate")
    fun findByUserSeqAndGSeqAndCertifyDate(@Param("userSeq")userSeq: Int,@Param("gSeq") gSeq: Int,@Param("certifyDate")certifyDate: LocalDateTime): Certify?

}