package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate

@Entity
@Table(name = "prize")
open class Prize(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "prize_seq")
        var prizeSeq: Int = 0,

        @Column(name = "prize_name")
        var prizeName: String? = null,

        @Column(name = "prize_money")
        var prizeMoney: Int? = null,

        @Column(name = "prize_date")
        var prizeDate: LocalDate? = null,

        @JsonBackReference(value = "user-prize")
        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "user_seq", referencedColumnName = "user_seq", nullable = true)
        var user: User? = null,


        @JsonBackReference(value = "greening-prize")
        @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "g_seq", referencedColumnName = "g_seq", nullable = true)
        @OnDelete(action = OnDeleteAction.SET_NULL)
        var greening: Greening? = null,


        @JsonBackReference(value = "participate-prize")
        @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "p_seq", referencedColumnName = "p_seq")
        @OnDelete(action = OnDeleteAction.SET_NULL)
        var participate: Participate? = null
)