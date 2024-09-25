package com.example.greening.repository

import com.example.greening.domain.item.Ranking
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class RankingRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun save(ranking: Ranking) {
        if(ranking.rankSeq == 0) {
            em.persist(ranking)
        }else{
            em.merge(ranking)
        }
    }

    fun delete(ranking: Ranking) {
        if (em.contains(ranking)) {
            em.remove(ranking)
        } else {
            em.remove(em.merge(ranking))
        }
    }

    fun deleteById(id: Int) {
        val ranking = findOne(id)
        if (ranking != null) {
            delete(ranking)
        }
    }

    fun findOne(id : Int) : Ranking?{
        return em.find(Ranking::class.java, id)
    }

    fun findAll() : List<Ranking>{
        return try{
            em.createQuery("select Ranking from Ranking", Ranking::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }

    fun findById(rankSeq: Int): Ranking? {
        return try{
            em.createQuery("select Ranking from Ranking where Ranking.rankSeq = :rankSeq", Ranking::class.java)
                    .setParameter("rankSeq", rankSeq)
                    .singleResult
        }catch(e: IllegalStateException){
            null
        }
    }

    fun findByUserSeq(userSeq: Int): List<Ranking> {
        return try{
            em.createQuery("select Ranking from Ranking where Ranking.user.userSeq = :userSeq", Ranking::class.java)
                    .setParameter("userSeq", userSeq)
                    .resultList
        }catch(e: Exception){
            emptyList()
        }
    }
}