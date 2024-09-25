package com.example.greening.service

import com.example.greening.domain.item.Prize
import com.example.greening.repository.GreeningRepository
import com.example.greening.repository.ParticipateRepository
import com.example.greening.repository.PrizeRepository
import com.example.greening.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class PrizeService(private val prizeRepository: PrizeRepository,
                   private val userRepository: UserRepository,
                   private val greeningRepository: GreeningRepository,
                   private val participateRepository: ParticipateRepository){

    @Transactional
    fun savePrize(userEmail: String, gSeq:Int, prizeMoney :Int) : Prize? {
        val user = userRepository.findByEmail(userEmail)
        val greening = greeningRepository.findById(gSeq)
        if (user != null && greening != null) {
            val participate = participateRepository.findByUserSeqAndGSeq(user.userSeq, greening.gSeq)
            if(participate != null){
                val existingPrize = prizeRepository.findByUserSeqAndGSeq(user.userSeq, greening.gSeq)
                if(existingPrize != null){
                    return null
                }
                val prize = Prize(
                        user = user,
                        greening = greening,
                        participate = participate,
                        prizeName = greening.gName,
                        prizeDate = LocalDate.now(),
                        prizeMoney = prizeMoney)
                return prizeRepository.save(prize)
            }
        }
        return null
    }

    @Transactional
    fun saveNewPrize(prize: Prize) : Prize? {
        if(prize != null){
            return prizeRepository.save(prize)
        }
        return null
    }

    @Transactional
    fun updatePrize(prizeSeq: Int, newPrize: Prize) {
        val existingPrize = prizeRepository.findById(prizeSeq).orElse(null)
        if (existingPrize != null) { // 필드를 직접 업데이트
            existingPrize.prizeMoney = newPrize.prizeMoney ?: existingPrize.prizeMoney
            prizeRepository.save(existingPrize)
        } else {
            throw IllegalStateException("상금이 존재하지 않습니다.")
        }
    }


    @Transactional
    fun deletePrize(prizeSeq: Int) {
        prizeRepository.deleteById(prizeSeq)
    }

    fun findPrize(): List<Prize> {
        return prizeRepository.findAll()
    }

    fun findById(prizeSeq: Int): Prize? {
        return prizeRepository.findById(prizeSeq).orElse(null)
    }

    fun findByParticipate_PSeq(pSeq: Int): Prize? {
        return prizeRepository.findByParticipate_PSeq(pSeq)
    }

    fun findByUserSeq(userSeq: Int): List<Prize> {
        return prizeRepository.findByUser_UserSeq(userSeq)
    }
}