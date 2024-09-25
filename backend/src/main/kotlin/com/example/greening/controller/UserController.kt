package com.example.greening.controller

import com.example.greening.domain.item.User
import com.example.greening.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping("/byId/{id}")
    fun getUserById(@PathVariable id: Int): ResponseEntity<User> {
        val user = userService.findOne(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/byEmail/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userService.findByEmail(email)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    @GetMapping("/byPhone/{phone}")
    fun getUserByPhone(@PathVariable phone: String): ResponseEntity<User> {
        val user = userService.findByPhone(phone)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createUser(@RequestBody user: User) : ResponseEntity<User>{
        userService.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @PutMapping("/update/{userSeq}")
    fun updateUser(@PathVariable userSeq:Int, @RequestBody user: User) : ResponseEntity<User>{
        val updateUser = userService.updateUser(userSeq,user)
        return ResponseEntity.ok(updateUser)
    }

    @PostMapping("/delete/{userSeq}")
    fun deleteUser(@PathVariable userSeq: Int): String {
        userService.deleteUser(userSeq)
        return "redirect:/users/listPage" // 삭제 후 회원 목록 페이지로 리디렉션
    }

    @PostMapping("/deleteByEmail/{userEmail}")
    fun deleteUserByEmail(@PathVariable userEmail: String): ResponseEntity<User> {
       val deleteUser = userService.deleteUserByEmail(userEmail)
        return ResponseEntity.ok(deleteUser)
    }

    @GetMapping("/list")
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.findUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/listPage")
    fun listUsers(model: Model): String {
        val users = userService.findUsers()
        model.addAttribute("users", users)
        return "userList"
    }

    @GetMapping("/seqByEmail/{email}")
    fun getUserSeqByEmail(@PathVariable email: String): ResponseEntity<Int> {
        val userSeq = userService.findUserSeqByEmail(email)
        return if (userSeq != null) {
            ResponseEntity.ok(userSeq)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/wCountByEmail/{userEmail}")
    fun getUserWCountByEmail(@PathVariable userEmail: String): ResponseEntity<Int> {
        val userWCount = userService.findUserWCountByEmail(userEmail)
        return if (userWCount != null) {
            ResponseEntity.ok(userWCount)
        } else {
            ResponseEntity.notFound().build()
        }
    }


}