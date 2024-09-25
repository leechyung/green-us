package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "review")
open class Review(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "review_seq")
        var reviewSeq: Int = 0,

        @JsonBackReference(value = "user-review")
        @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "user_seq", referencedColumnName = "user_seq")
        var user: User? = null,

        @JsonBackReference(value = "greening-review")
        @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "g_seq", referencedColumnName = "g_seq")
        var greening: Greening? = null,

        @Column(name = "review_content")
        var reviewContent: String? = null,

        @Column(name = "review_date")
        var reviewDate: LocalDate? = null,

        @Column(name = "review_rate")
        var reviewRate: Float? = null
)