package com.example.greening.controller

import com.example.greening.domain.item.Ranking
import com.example.greening.service.RankingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rank")
class RankingController(private val rankingService: RankingService) {

    @GetMapping("/byId/{id}")
    fun getRankingById(@PathVariable id: Int): ResponseEntity<Ranking> {
        val ranking = rankingService.findOne(id)
        return if (ranking != null) {
            ResponseEntity.ok(ranking)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createRanking(@RequestBody ranking: Ranking): ResponseEntity<Ranking> {
        rankingService.saveRanking(ranking)
        return ResponseEntity.status(HttpStatus.CREATED).body(ranking)
    }

    @PutMapping("/update/{rankSeq}")
    fun updateRanking(@PathVariable rankSeq: Int, @RequestBody newRanking: Ranking): ResponseEntity<Ranking> {
        return try {
            rankingService.updateRanking(rankSeq, newRanking)
            ResponseEntity.ok(newRanking)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/delete/{rankSeq}")
    fun deleteRanking(@PathVariable rankSeq: Int) {
        rankingService.deleteRanking(rankSeq)
    }

    @GetMapping("/list")
    fun getAllRanking(): ResponseEntity<List<Ranking>> {
        val prize = rankingService.findRanking()
        return ResponseEntity.ok(prize)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getRankingByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Ranking>> {
        val ranking = rankingService.findByUserSeq(userSeq)
        return ResponseEntity.ok(ranking)
    }
}