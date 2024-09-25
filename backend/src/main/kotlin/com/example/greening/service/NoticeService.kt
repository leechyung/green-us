package com.example.greening.service

import com.example.greening.domain.item.Notice
import com.example.greening.repository.NoticeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NoticeService(private val noticeRepository: NoticeRepository){

    @Transactional
    fun saveNotice(notice: Notice) {
        noticeRepository.save(notice)
    }

    @Transactional
    fun updateNotice(noticeSeq: Int, newNotice: Notice) {
        val existingNotice = noticeRepository.findById2(noticeSeq)
        if (existingNotice != null) {
            existingNotice.noticeTitle = newNotice.noticeTitle ?: existingNotice.noticeTitle
            existingNotice.noticeContent = newNotice.noticeContent ?: existingNotice.noticeContent
            existingNotice.noticeDate = newNotice.noticeDate ?: existingNotice.noticeDate

            noticeRepository.save(existingNotice)
        } else {
            throw IllegalStateException("공지사항이 존재하지 않습니다.")
        }
    }

    @Transactional
    fun deleteNotice(noticeSeq: Int) {
        noticeRepository.deleteById(noticeSeq)
    }

    fun findNotice(): List<Notice> {
        return noticeRepository.findAll()
    }

    fun findOne(noticeSeq: Int): Notice? {
        return noticeRepository.findById2(noticeSeq)
    }

    fun findByTitleContaining(title: String): List<Notice> {
        return noticeRepository.findByTitleContaining(title)
    }
}