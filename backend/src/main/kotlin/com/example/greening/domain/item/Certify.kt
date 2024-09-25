package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="certify")
open class Certify (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="certify_seq")
        var certifySeq:Int=0,

        @Column(name="certify_img")
        var certifyImg:String?= null,

        @Column(name="certify_date")
        var certifyDate:LocalDateTime? = null,

        @JsonBackReference(value = "user-certify")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="user_seq", referencedColumnName = "user_seq")
        var user: User? = null,

        @JsonBackReference(value = "greening-certify")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="g_seq", referencedColumnName = "g_seq")
        var greening: Greening? = null,

        @JoinColumn(name="p_seq", referencedColumnName = "p_seq")
        var pSeq:Int? = null,

        @JsonManagedReference(value = "report-certify")
        @OneToOne(mappedBy = "certify", cascade = [CascadeType.ALL], orphanRemoval = true)
        var report: Report? = null

)