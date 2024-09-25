package com.example.greening.repository

import com.example.greening.domain.item.Greening
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class GreeningRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun save(greening: Greening) {
        if(greening.gSeq == 0) {
            em.persist(greening)
        }else{
            em.merge(greening)
        }
    }

    fun delete(greening: Greening) {
        if (em.contains(greening)) {
            em.remove(greening)
        } else {
            em.remove(em.merge(greening))
        }
    }

    fun deleteById(id: Int) {
        val greening = findOne(id)
        if (greening != null) {
            delete(greening)
        }
    }

    fun findOne(id : Int) : Greening?{
        return try{
            em.find(Greening::class.java, id)
        }catch(e: Exception){
            null
        }
    }

    fun findAll() : List<Greening>{
        return try {
            em.createQuery("select g from Greening g", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findById(gSeq: Int): Greening? {
        return try {
            em.createQuery("select g from Greening g where g.gSeq = :gSeq", Greening::class.java)
                    .setParameter("gSeq", gSeq)
                    .singleResult
        } catch (e: NoResultException) {
            null
        }
    }



    fun findBygKind(gKind: Int): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gKind = :gKind", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findDoGreenBygKind(): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gKind in (1,3)", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findBuyGreenBygKind(): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gKind in (2,4)", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findNewGreen(): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gStartDate > CURRENT_DATE order by g.gSeq desc", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findByGEndDate(date: LocalDate): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gEndDate = :date", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findPopGreen(): List<Greening> {
        return try {
            em.createQuery("select g from Greening g where g.gStartDate > CURRENT_DATE order by g.gMemberNum desc", Greening::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findByUserId(userSeq: Int): List<Greening> {
        return try{
            em.createQuery("select g from Greening g where g.user.userSeq = :userSeq", Greening::class.java)
                    .setParameter("userSeq", userSeq)
                    .resultList
        }catch(e: Exception){
            emptyList()
        }
    }


}