package com.example.greening.repository

import com.example.greening.domain.item.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository : JpaRepository<Admin, Int> {
    fun findByAdminId(adminId: String): Admin?
}