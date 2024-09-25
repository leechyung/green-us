package com.example.greening.service

import com.example.greening.domain.item.Report
import com.example.greening.repository.CertifyRepository
import com.example.greening.repository.ReportRepository
import com.example.greening.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ReportService(
        private val certifyRepository: CertifyRepository,
        private val userRepository: UserRepository,
        private val userService: UserService,
        private val reportRepository: ReportRepository) {

    @Transactional
    fun saveReport(userEmail:String, certifySeq: Int): ResponseEntity<Report>{
        return try {
            val user = userRepository.findByEmail(userEmail)
            val certify = if (user != null) certifyRepository.findById(certifySeq).orElse(null) else null
            if (certify != null && user != null && user.userWCount!! < 5) {
                val report = Report(certify = certify, reportDate = LocalDateTime.now())
                ResponseEntity.status(HttpStatus.CREATED).body(reportRepository.save(report))
                //            reportRepository.save(report)
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }
        }catch (e: DataIntegrityViolationException){
            ResponseEntity.status(HttpStatus.CONFLICT).body(null)
        }catch(e:Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @Transactional
    fun updateReport(reportSeq: Int, newReport: Report) {
        val existingReport = reportRepository.findById(reportSeq).orElse(null)
                ?: throw IllegalStateException("신고가 존재하지 않습니다.")
        existingReport.reportDate = newReport.reportDate
        existingReport.reportResult = newReport.reportResult
        existingReport.reportSeq = newReport.reportSeq
        existingReport.certify = newReport.certify

        reportRepository.save(existingReport)
    }


    @Transactional
    fun deleteReport(reportSeq: Int) {
        if (reportRepository.existsById(reportSeq)) {
            reportRepository.deleteById(reportSeq)
        } else {
            throw IllegalStateException("신고가 존재하지 않습니다.")
        }

    }

    fun findReport(): List<Report> {
        return reportRepository.findAll()
    }

    fun findReportById(reportSeq: Int): Report? {
        return reportRepository.findById(reportSeq).orElse(null)
    }

    fun findReportsByCertifySeq(certifySeq: Int): List<Report> {
        return reportRepository.findByCertifySeq(certifySeq)
    }
}