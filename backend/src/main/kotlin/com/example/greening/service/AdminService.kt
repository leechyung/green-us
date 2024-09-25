package com.example.greening.service

import com.example.greening.domain.item.Admin
import com.example.greening.repository.AdminRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminService(private val adminRepository: AdminRepository) {

    fun authenticateAdmin(adminId: String, adminPwd: String): Admin? {
        val admin = adminRepository.findByAdminId(adminId)
        return if (admin != null && admin.adminPwd == adminPwd) {
            admin
        } else {
            null
        }
    }
}