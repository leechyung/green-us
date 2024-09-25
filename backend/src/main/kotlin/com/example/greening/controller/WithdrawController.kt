package com.example.greening.controller

import com.example.greening.domain.item.Withdraw
import com.example.greening.service.WithdrawService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/withdraw")
class WithdrawController(private val withdrawService: WithdrawService) {

    @GetMapping("/byId/{id}")
    fun getWithdrawById(@PathVariable id: Int): ResponseEntity<Withdraw> {
        val withdraw = withdrawService.findOne(id)
        return if (withdraw != null) {
            ResponseEntity.ok(withdraw)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createWithdraw(@RequestBody withdraw: Withdraw): ResponseEntity<Withdraw> {
        withdrawService.saveWithdraw(withdraw)
        return ResponseEntity.status(HttpStatus.CREATED).body(withdraw)
    }

    @PutMapping("/update/{withdrawSeq}")
    fun updateWithdraw(@PathVariable withdrawSeq: Int, @RequestBody newWithdraw: Withdraw): ResponseEntity<Withdraw> {
        return try {
            withdrawService.updateWithdraw(withdrawSeq, newWithdraw)
            ResponseEntity.ok(newWithdraw)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/delete/{withdrawSeq}")
    fun deleteWithdraw(@PathVariable withdrawSeq: Int) {
        withdrawService.deleteWithdraw(withdrawSeq)
    }

    @GetMapping("/list")
    fun getAllWithdraw(): ResponseEntity<List<Withdraw>> {
        val withdraw = withdrawService.findWithdraw()
        return ResponseEntity.ok(withdraw)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getWithdrawByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Withdraw>> {
        val withdraw = withdrawService.findByUserSeq(userSeq)
        return ResponseEntity.ok(withdraw)
    }
}
