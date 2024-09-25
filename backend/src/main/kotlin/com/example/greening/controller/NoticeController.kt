package com.example.greening.controller

import com.example.greening.domain.item.Notice
import com.example.greening.service.NoticeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/notice")
class NoticeController(private val noticeService: NoticeService) {

    @GetMapping("/list")
    fun getAllNotice(): ResponseEntity<List<Notice>> {
        val notice = noticeService.findNotice()
        return ResponseEntity.ok(notice)
    }

    @GetMapping("/listPage")
    fun getAllNotice(model: Model): String {
        val notices = noticeService.findNotice()
        model.addAttribute("notices", notices)
        return "noticeList" // 공지사항 목록 페이지
    }

    @GetMapping("/add")
    fun getAddNoticePage(): String {
        return "noticeAdd" // 공지사항 추가 페이지
    }

    @PostMapping("/new")
    fun createNotice(@ModelAttribute notice: Notice): String {
        noticeService.saveNotice(notice)
        return "redirect:/notice/listPage" // 추가 후 목록 페이지로 리다이렉트
    }

    @PostMapping("/delete/{noticeSeq}")
    fun deleteNotice(@PathVariable noticeSeq: Int): String {
        noticeService.deleteNotice(noticeSeq)
        return "redirect:/notice/listPage" // 삭제 후 목록 페이지로 리다이렉트
    }



    @GetMapping("/edit/{noticeSeq}")
    fun getEditNoticePage(@PathVariable noticeSeq: Int, model: Model): String {
        val notice = noticeService.findOne(noticeSeq)
        model.addAttribute("notice", notice)
        return "noticeEdit" // 공지사항 수정 페이지
    }


    @PutMapping("/update/{noticeSeq}")
    fun updateNotice(@PathVariable noticeSeq: Int, @RequestBody newNotice: Notice): ResponseEntity<Notice> {
        return try {
            noticeService.updateNotice(noticeSeq, newNotice)
            ResponseEntity.ok(newNotice)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }
}