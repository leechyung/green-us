package com.example.greening.controller

import com.example.greening.service.AdminService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admins")
class AdminController(private val adminService: AdminService) {

    @PostMapping("/login")
    fun login(@RequestParam adminId: String, @RequestParam adminPwd: String, request: HttpServletRequest, model: Model): String {
        val admin = adminService.authenticateAdmin(adminId, adminPwd)
        return if (admin != null) {
            request.session.setAttribute("adminId", admin.adminId)
            "redirect:/admins/home"  // 로그인 성공 시 홈으로 리디렉션
        } else {
            model.addAttribute("error", "Invalid credentials")
            "login"  // 로그인 페이지를 반환
        }
    }

    @GetMapping("/home")
    fun home(request: HttpServletRequest, model: Model): String {
        val adminId = request.session.getAttribute("adminId") as? String
        return if (adminId != null) {
            model.addAttribute("adminId", adminId)
            "home"  // home.html을 반환
        } else {
            "redirect:/admins/login"  // 로그인 필요 시 로그인 페이지로 리디렉션
        }
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"  // login.html을 반환
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.session.invalidate()  // 세션 무효화
        return "redirect:/admins/login"  // 로그인 페이지로 리디렉션
    }
}
