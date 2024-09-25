package com.example.greening.repository


import com.example.greening.domain.item.Greening
import com.example.greening.domain.item.Participate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ParticipateRepository : JpaRepository<Participate, Int> {

    @Query("SELECT p FROM Participate p WHERE p.user.userSeq = :userSeq")
    fun findByUserSeq(@Param("userSeq") userSeq: Int): List<Participate>

    @Query("SELECT p FROM Participate p WHERE p.user.userSeq = :userSeq AND p.pComplete = 'N'")
    fun findNByUserSeq(@Param("userSeq") userSeq: Int): List<Participate>

    @Query("SELECT p FROM Participate p WHERE p.greening.gSeq = :gSeq")
    fun findBygSeq(@Param("gSeq") gSeq: Int): List<Participate>

    @Query("SELECT p.greening FROM Participate p WHERE p.pSeq = :pSeq")
    fun findByParticipateId(@Param("pSeq") gSeq: Int): Greening?

    @Query("SELECT p.greening FROM Participate p WHERE p.user.userSeq = :userSeq")
    fun findGreeningByUserSeq(@Param("userSeq") userSeq: Int): List<Greening>

    @Query("SELECT p.greening FROM Participate p WHERE p.user.userSeq = :userSeq AND p.pComplete = 'Y'")
    fun findYGreeningByUserSeq(@Param("userSeq") userSeq: Int): List<Greening>

    @Query("SELECT p.pSeq FROM Participate p WHERE p.user.userSeq = :userSeq AND p.greening.gSeq = :gSeq")
    fun findPSeqByGSeqAndUserSeq(@Param("userSeq") userSeq: Int, @Param("gSeq") gSeq: Int): Int?

    @Query("SELECT p FROM Participate p WHERE p.user.userSeq = :userSeq AND p.greening.gSeq = :gSeq")
    fun findByUserSeqAndGSeq(@Param("userSeq") userSeq: Int, @Param("gSeq") gSeq: Int): Participate?

    @Query("SELECT p.pSeq FROM Participate p WHERE p.user.userEmail = :userEmail AND p.greening.gSeq = :gSeq")
    fun findPSeqByGSeqAndUserEmail(@Param("userEmail") userEmail: String, @Param("gSeq") gSeq: Int): Int?
}