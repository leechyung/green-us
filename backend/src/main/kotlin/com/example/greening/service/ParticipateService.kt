package com.example.greening.service

import com.example.greening.domain.item.Greening
import com.example.greening.domain.item.Participate
import com.example.greening.repository.GreeningRepository
import com.example.greening.repository.ParticipateRepository
import com.example.greening.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ParticipateService(private val participateRepository: ParticipateRepository, private val userRepository: UserRepository, private val greeningRepository: GreeningRepository) {

    @Transactional
    fun saveParticipate(participate: Participate) {
        val user = participate.user?.let { userRepository.findById(it.userSeq)}
        val greening = participate.greening?.let { greeningRepository.findById(it.gSeq)}
        if (user != null) {
            participate.user = user
        }
        if (greening != null) {
            participate.greening = greening
        }
        participateRepository.save(participate)
    }

    @Transactional
    fun updateParticipate(pSeq: Int) {
        val existingParticipate = if(pSeq > -1)participateRepository.findById(pSeq).orElse(null) else null
        if(existingParticipate != null ) {
            if (existingParticipate.pComplete == "N") {
                existingParticipate.pCount = existingParticipate.pCount!! + 1
                if (existingParticipate.greening!!.gNumber!! == existingParticipate.pCount!!)
                    existingParticipate.pComplete = "Y"
                if ((existingParticipate.greening!!.gNumber!!)* 0.7 >= existingParticipate.pCount!!)
                    existingParticipate.pComplete = "Y"
                participateRepository.save(existingParticipate)
            } else {
                throw IllegalStateException("가능한 횟수를 넘었습니다.")
            }
        }else{
            throw IllegalStateException("참여가 존재하지 않습니다.")
        }
    }

    @Transactional
    fun warnedParticipation(pSeq: Int) {
        val existingParticipate = if(pSeq > -1)participateRepository.findById(pSeq).orElse(null) else null
        if(existingParticipate != null ) {
            if (existingParticipate.pCount!! > 0) {
                existingParticipate.pCount = existingParticipate.pCount!! - 1
                if (existingParticipate.greening!!.gNumber!! == existingParticipate.pCount!!) {
                    existingParticipate.pComplete = "Y"
                }else if (existingParticipate.greening!!.gNumber!! > existingParticipate.pCount!!) {
                    existingParticipate.pComplete = "N"
                }
                participateRepository.save(existingParticipate)
            } else {
                existingParticipate.pCount = 0
                participateRepository.save(existingParticipate)
            }
        }else{
            throw IllegalStateException("참여가 존재하지 않습니다.")
        }
    }

    @Transactional
    fun deleteParticipate(pSeq: Int) {
        participateRepository.deleteById(pSeq)
    }

    fun findParticipate(): List<Participate> {
        return participateRepository.findAll()
    }

    fun findOne(pSeq: Int): Participate? {
        return participateRepository.findById(pSeq).orElse(null)
    }

    fun findById(pSeq: Int): Participate? {
        return participateRepository.findById(pSeq).orElse(null)
    }

    fun findGreening(pSeq: Int): Greening? {
        return participateRepository.findByParticipateId(pSeq)
    }

    fun findByUserSeq(userSeq: Int): List<Participate> {
        return participateRepository.findByUserSeq(userSeq)
    }

    fun findNByUserSeq(userSeq: Int): List<Participate> {
        return participateRepository.findNByUserSeq(userSeq)
    }

    fun findBygSeq(gSeq: Int): List<Participate> {
        return participateRepository.findBygSeq(gSeq)
    }

    fun findGreeningByUserSeq(userSeq: Int): List<Greening> {
        return participateRepository.findGreeningByUserSeq(userSeq)
    }

    fun findYGreeningByUserSeq(userSeq: Int): List<Greening> {
        return participateRepository.findYGreeningByUserSeq(userSeq)
    }

    fun findByUserSeqAndgSeq(userSeq: Int, gSeq: Int): Participate?{
        return participateRepository.findByUserSeqAndGSeq(userSeq,gSeq)
    }
    fun findPSeqByUserSeqAndgSeq(userSeq: Int, gSeq: Int): Int? {
        return participateRepository.findPSeqByGSeqAndUserSeq(userSeq,gSeq)
    }

    fun findPSeqByGSeqAndUserEmail(userEmail: String, gSeq: Int): Int? {
        return participateRepository.findPSeqByGSeqAndUserEmail(userEmail,gSeq)
    }

}