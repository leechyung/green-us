package com.example.greening.service

import com.example.greening.domain.item.Prize
import com.example.greening.domain.item.Ranking
import com.example.greening.repository.RankingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RankingService(private val rankingRepository: RankingRepository) {

    @Transactional
    fun saveRanking(ranking: Ranking) {
        rankingRepository.save(ranking)
    }

    @Transactional
    fun updateRanking(rankSeq: Int, newRanking: Ranking) {
        val existingRanking = rankingRepository.findOne(rankSeq)
        if (existingRanking != null) { // 필드를 직접 업데이트
            existingRanking.user.userSeq = newRanking.user.userSeq

            rankingRepository.save(existingRanking)
        } else {
            throw IllegalStateException("랭킹이 존재하지 않습니다.")
        }
    }


    @Transactional
    fun deleteRanking(rankSeq: Int) {
        rankingRepository.deleteById(rankSeq)
    }

    fun findRanking(): List<Ranking> {
        return rankingRepository.findAll()
    }

    fun findOne(rankSeq: Int): Ranking? {
        return rankingRepository.findOne(rankSeq)
    }

    fun findById(rankSeq: Int): Ranking? {
        return rankingRepository.findById(rankSeq)
    }

    fun findByUserSeq(userSeq: Int): List<Ranking> {
        return rankingRepository.findByUserSeq(userSeq)
    }
}