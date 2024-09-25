package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "participate")
open class Participate(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "p_seq")
        var pSeq: Int = 0,

        @JsonBackReference(value = "greening-participates")
        @ManyToOne(cascade = [CascadeType.MERGE], optional = true)
        @JoinColumn(name = "g_seq", referencedColumnName = "g_seq", nullable = true)
        var greening: Greening? = null,

        @JsonBackReference(value = "user-participates")
        @ManyToOne(cascade = [CascadeType.MERGE], optional = true)
        @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = true)
        var user: User? = null,

        @Column(name = "p_complete")
        var pComplete: String? = null,

        @Column(name = "p_count")
        var pCount: Int? = null
)