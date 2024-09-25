package com.example.greening.service

import com.example.greening.domain.item.Greening
import com.example.greening.repository.GreeningRepository
import com.example.greening.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
@Transactional(readOnly = true)
class GreeningService(private val greeningRepository: GreeningRepository, private val userRepository: UserRepository) {

    @Transactional
    fun saveGreening(greening: Greening) {
        val user = greening.user?.let { userRepository.findById(it.userSeq)}
        if (user != null) {
            greening.user = user
        }
        return greeningRepository.save(greening)
    }

    @Transactional
    fun updateGreening(gSeq: Int, newGreening: Greening) {
        val existingGreening = greeningRepository.findById(gSeq)
        if (existingGreening != null) {
            existingGreening.gName = newGreening.gName ?: existingGreening.gName
            existingGreening.gStartDate = newGreening.gStartDate ?: existingGreening.gStartDate
            existingGreening.gEndDate = newGreening.gEndDate ?: existingGreening.gEndDate
            existingGreening.gCertiWay = newGreening.gCertiWay ?: existingGreening.gCertiWay
            existingGreening.gInfo = newGreening.gInfo ?: existingGreening.gInfo
            existingGreening.gMemberNum = newGreening.gMemberNum ?: existingGreening.gMemberNum
            existingGreening.gFreq = newGreening.gFreq ?: existingGreening.gFreq
            existingGreening.gDeposit = newGreening.gDeposit ?: existingGreening.gDeposit
            existingGreening.gTotalCount = newGreening.gTotalCount ?: existingGreening.gTotalCount
            existingGreening.gNumber = newGreening.gNumber ?: existingGreening.gNumber

            greeningRepository.save(existingGreening)
        } else {
            throw IllegalStateException("그리닝이 존재하지 않습니다.")
        }
    }


    @Transactional
    fun deleteGreening(gSeq: Int) {
        greeningRepository.deleteById(gSeq)
    }

    fun findGreening(): List<Greening> {
        return greeningRepository.findAll()
    }

    fun findOne(gSeq: Int): Greening? {
        return greeningRepository.findOne(gSeq)
    }

    fun findById(gSeq: Int): Greening? {
        return greeningRepository.findById(gSeq)
    }

    fun findBygKind(gKind: Int): List<Greening>{
        return greeningRepository.findBygKind(gKind)
    }

    fun findDoGreenBygKind(): List<Greening>{
        return greeningRepository.findDoGreenBygKind()
    }

    fun findBuyGreenBygKind(): List<Greening>{
        return greeningRepository.findBuyGreenBygKind()
    }

    fun findNewGreen(): List<Greening>{
        return greeningRepository.findNewGreen()
    }

    fun findPopGreen(): List<Greening>{
        return greeningRepository.findPopGreen()
    }

    fun findByGEndDate(date: LocalDate): List<Greening> {
        return greeningRepository.findByGEndDate(date)
    }

    fun findByUserId(userSeq: Int): List<Greening> {
        return greeningRepository.findByUserId(userSeq)
    }

}