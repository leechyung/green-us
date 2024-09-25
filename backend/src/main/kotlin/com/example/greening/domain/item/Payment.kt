package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "payment")
open class Payment(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "payment_seq")
        var paymentSeq: Int = 0,

        @JsonBackReference(value="user-payments")
        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "user_seq", referencedColumnName = "user_seq")
        var user: User? = null,

        @Column(name = "payment_content")
        var paymentContent: String? = null,

        @Column(name = "payment_method")
        var paymentMethod: String? = null,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Column(name = "payment_date")
        var paymentDate: LocalDate? = null,

        @Column(name = "payment_money")
        var paymentMoney: Int? = null
)