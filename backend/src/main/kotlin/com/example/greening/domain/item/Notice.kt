package com.example.greening.domain.item

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notice")
open class Notice(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="notice_seq")
        var noticeSeq:Int = 0,

        @JoinColumn(name = "admin_seq", referencedColumnName = "admin_seq", nullable = false)
        var adminSeq:Int? = null,

        @Column(name="notice_title")
        var noticeTitle:String?= null,

        @Column(name="notice_content")
        var noticeContent:String? = null,

        @Column(name="notice_date")
        var noticeDate: LocalDate? = null
)