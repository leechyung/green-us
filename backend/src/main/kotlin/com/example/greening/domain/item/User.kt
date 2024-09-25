package com.example.greening.domain.item

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name="user")
open class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_seq")
    var userSeq : Int=0,

    @Column(name="user_name")
    var userName : String?=null,

    @Column(name="user_email")
    var userEmail : String?=null,

    @Column(name="user_addr")
    var userAddr : String?=null,

    @Column(name="user_addr_detail")
    var userAddrDetail : String?=null,

    @Column(name="user_phone")
    var userPhone : String?=null,

    @Column(name="user_photo")
    var userPhoto : String?=null,

    @Column(name="user_pedometer")
    var userPedometer : Int ?= 0,

    @Column(name="user_w_count")
    var userWCount : Int?= 0,

    @JsonManagedReference(value = "user-participates")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val participates: List<Participate> = mutableListOf(),

    @JsonBackReference(value = "admin-user")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_seq", referencedColumnName = "admin_seq", nullable = true)
    var admins: Admin? = null,

    @JsonManagedReference(value = "user-review")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val reviews: List<Review> = mutableListOf(),

    @JsonManagedReference(value = "user-payments")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val payments: List<Payment> = mutableListOf(),

    @JsonManagedReference(value = "user-greening")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val greening: List<Greening> = mutableListOf(),

    @JsonManagedReference(value = "user-certify")
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val certifies: List<Certify> = mutableListOf()
)