package com.example.greening.controller


import com.example.greening.domain.item.Certify
import com.example.greening.domain.item.Greening
import com.example.greening.service.CertifyService
import com.example.greening.service.ParticipationAlreadyCompletedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDateTime

@Controller
@RequestMapping("/certify")
class CertifyController(private val certifyService: CertifyService) {

    @GetMapping("/byId/{id}")
    fun getCertifyById(@PathVariable id: Int): ResponseEntity<Certify> {
        val certify = certifyService.findOne(id)
        return if (certify != null) {
            ResponseEntity.ok(certify)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createCertify(@RequestParam userEmail:String,@RequestParam gSeq: Int,@RequestParam certifyDate: LocalDateTime): ResponseEntity<Certify> {
        return try {
            val certify = certifyService.saveCertify(userEmail, gSeq, certifyDate)
            if (certify != null) {
                ResponseEntity.status(HttpStatus.CREATED).body(certify)
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build() // 기본적인 오류 처리
            }
        } catch (ex: ParticipationAlreadyCompletedException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(null)  // 409 상태 코드 반환
        }
    }

    @PutMapping("/update/{certifySeq}")
    fun updateCertify(@PathVariable certifySeq: Int, @RequestBody newCertify: Certify): ResponseEntity<Certify> {
        return try {
            certifyService.updateCertify(certifySeq, newCertify)
            ResponseEntity.ok(newCertify)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/list")
    fun getAllCertify(): ResponseEntity<List<Certify>> {
        val certifies = certifyService.findCertify()
        return ResponseEntity.ok(certifies)
    }

    @GetMapping("/bypSeq/{pSeq}")
    fun getCertifyBypSeq(@PathVariable pSeq: Int): ResponseEntity<List<Certify>> {
        val certifies = certifyService.findBypSeq(pSeq)
        return ResponseEntity.ok(certifies)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getCertifyByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Certify>> {
        val certifies = certifyService.findByUserId(userSeq)
        return ResponseEntity.ok(certifies)
    }

    @GetMapping("/byGreeningUser/{userSeq}/{gSeq}")
    fun getCertifyByUserSeqAndGSeq(@PathVariable userSeq: Int, @PathVariable gSeq: Int): ResponseEntity<List<Certify>> {
        val certifies = certifyService.findByUserSeqAndGSeq(userSeq, gSeq)
        return ResponseEntity.ok(certifies)
    }
    @PostMapping("/delete/{certifySeq}")
    fun deleteCertify(@PathVariable certifySeq: Int): String {
        return try {
            certifyService.deleteCertify(certifySeq)
            "redirect:/certify/list"
        } catch (e: Exception) {
            "redirect:/certify/list?error=true"
        }
    }
    @GetMapping("/listPage")
    fun getCertifyList(model: Model): String {
        val certifies = certifyService.findCertify()
        model.addAttribute("certifies", certifies)
        return "certifyList" // 인증 목록 페이지로 이동
    }

    @GetMapping("/byUSerEmailAndGSeq/{userEmail}/{gSeq}")
    fun findByUserEmailAndGSeq(@PathVariable userEmail:String, @PathVariable gSeq:Int):ResponseEntity<List<Certify>>{
        val certify = certifyService.findByUserEmailAndGSeq(userEmail, gSeq)
        return ResponseEntity.ok(certify)
    }

    @GetMapping("/byUserSeqAndGSeqAneCertifyDate/{userSeq}/{gSeq}/{CertifyDate}")
    fun findByUserSeqAndGSeqAndCertifyDate(@PathVariable userSeq: Int,@PathVariable gSeq: Int,@PathVariable CertifyDate: LocalDateTime): ResponseEntity<Certify> {
        val certify = certifyService.findByUserSeqAndGSeqAndCertifyDate(userSeq, gSeq, CertifyDate)
        return ResponseEntity.ok(certify)
    }

}