package com.example.greening.controller

import com.example.greening.domain.item.Greening
import com.example.greening.domain.item.Participate
import com.example.greening.service.ParticipateService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/participate")
class ParticipateController(private val participateService: ParticipateService) {

    @GetMapping("/byId/{id}")
    fun getParticipateById(@PathVariable id: Int): ResponseEntity<Participate> {
        val participate = participateService.findOne(id)
        return if (participate != null) {
            ResponseEntity.ok(participate)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createParticipate(@RequestBody participate: Participate): ResponseEntity<Participate> {
        participateService.saveParticipate(participate)
        return ResponseEntity.status(HttpStatus.CREATED).body(participate)
    }

    @DeleteMapping("/delete/{pSeq}")
    fun deleteParticipate(@PathVariable pSeq: Int) {
        participateService.deleteParticipate(pSeq)
    }

    @PutMapping("/update/{pSeq}")
    fun updateParticipate(@PathVariable pSeq: Int): ResponseEntity<Int> {
        return try {
            participateService.updateParticipate(pSeq)
            ResponseEntity.ok(pSeq)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/list")
    fun getAllParticipate(): ResponseEntity<List<Participate>> {
        val participate = participateService.findParticipate()
        return ResponseEntity.ok(participate)
    }

    @GetMapping("/greening/byId/{id}")
    fun getParticipateByPId(@PathVariable id: Int): ResponseEntity<Greening> {
        val greening = participateService.findGreening(id)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getParticipateByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Participate>> {
        val participate = participateService.findByUserSeq(userSeq)
        return ResponseEntity.ok(participate)
    }

    @GetMapping("/NbyUserSeq/{userSeq}")
    fun getNParticipateByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Participate>> {
        val participate = participateService.findNByUserSeq(userSeq)
        return ResponseEntity.ok(participate)
    }

    @GetMapping("/byGSeq/{gSeq}")
    fun getParticipateBygSeq(@PathVariable gSeq: Int): ResponseEntity<List<Participate>> {
        val participate = participateService.findBygSeq(gSeq)
        return ResponseEntity.ok(participate)
    }

    @GetMapping("/GreeningByUserSeq/{userSeq}")
    fun findGreeningByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Greening>> {
        val greening = participateService.findGreeningByUserSeq(userSeq)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/YGreeningByUserSeq/{userSeq}")
    fun findYGreeningByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Greening>> {
        val greening = participateService.findYGreeningByUserSeq(userSeq)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/gSeqByUserAndGreening/{gSeq}/{userSeq}")
    fun findpSeqByUserSeqAndgSeq(@PathVariable userSeq: Int,@PathVariable gSeq: Int): ResponseEntity<Int> {
        val pSeq = participateService.findPSeqByUserSeqAndgSeq(userSeq,gSeq)
        return ResponseEntity.ok(pSeq)
    }

    @GetMapping("/ByUserSeqAndGSeq/{gSeq}/{userSeq}")
    fun findParticipateByUserSeqAndGSeq(@PathVariable userSeq: Int,@PathVariable gSeq: Int): ResponseEntity<Participate> {
        val participate = participateService.findByUserSeqAndgSeq(userSeq,gSeq)
        return ResponseEntity.ok(participate)
    }

    @GetMapping("/ByUserEmailAndGSeq/{gSeq}/{userEmail}")
    fun findPSeqByGSeqAndUserEmail(@PathVariable userEmail: String,@PathVariable gSeq: Int): ResponseEntity<Int> {
        val pSeq = participateService.findPSeqByGSeqAndUserEmail(userEmail,gSeq)
        return ResponseEntity.ok(pSeq)
    }
}
