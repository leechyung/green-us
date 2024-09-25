package com.example.greening.repository

import com.example.greening.domain.item.Report
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Int> {
    @Query("select re from Report re where re.certify.certifySeq = :certifySeq")
    fun findByCertifySeq(@Param("certifySeq") certifySeq: Int): List<Report>
}