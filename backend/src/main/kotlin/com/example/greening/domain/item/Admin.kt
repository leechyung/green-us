package com.example.greening.domain.item

import jakarta.persistence.*

@Entity
@Table(name = "admin")
open class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="admin_seq")
    var adminSeq:Int = 0,

    @Column(name="admin_id")
    var adminId:String?=null,

    @Column(name="admin_pwd")
    var adminPwd:String?=null
)