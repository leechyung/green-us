package com.example.greening.repository

import com.example.greening.domain.item.Admin
import com.example.greening.domain.item.Notice
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class NoticeRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun save(notice: Notice) {
        if(notice.noticeSeq == 0) {
            em.persist(notice)
        }else{
            em.merge(notice)
        }
    }

    fun delete(notice: Notice) {
        if (em.contains(notice)) {
            em.remove(notice)
        } else {
            em.remove(em.merge(notice))
        }
    }

    fun deleteById(id: Int) {
        val notice = findOne(id)
        if (notice != null) {
            delete(notice)
        }
    }

    fun findOne(id : Int) : Notice?{
        return em.find(Notice::class.java, id)
    }

    fun findAll() : List<Notice>{
        return try{
            em.createQuery("select n from Notice n", Notice::class.java).resultList
        }catch(e: Exception){
            emptyList()
        }
    }


    fun findById(noticeSeq: Int): Notice? {
        return try {
            em.createQuery("select n from Notice n where n = :noticeSeq", Notice::class.java)
                .setParameter("noticeSeq", noticeSeq)
                .singleResult
        }catch (e: NoResultException) {
            null
        }
    }

    fun findById2(id: Int): Notice? {
        return try {
            em.find(Notice::class.java, id)
        } catch (e: NoResultException) {
            null
        }
    }

    fun findByTitleContaining(title: String): List<Notice> {
        return try {
            em.createQuery("select n from Notice n where n.noticeTitle like :title", Notice::class.java)
                    .setParameter("title", "%$title%")
                    .resultList
        }catch(e: Exception){
            emptyList()
        }
    }
}