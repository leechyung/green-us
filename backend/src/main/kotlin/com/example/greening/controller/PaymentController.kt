package com.example.greening.controller

import com.example.greening.domain.item.Payment
import com.example.greening.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/payment")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping("/byId/{id}")
    fun getPaymentById(@PathVariable id: Int): ResponseEntity<Payment> {
        val payment = paymentService.findOne(id)
        return if (payment != null) {
            ResponseEntity.ok(payment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createPayment(@RequestBody payment: Payment): ResponseEntity<Payment> {
        paymentService.savePayment(payment)
        return ResponseEntity.status(HttpStatus.CREATED).body(payment)
    }

    @PutMapping("/update/{paymentSeq}")
    fun updatePayment(@PathVariable paymentSeq: Int, @RequestBody newPayment: Payment): ResponseEntity<Payment> {
        return try {
            paymentService.updatePayment(paymentSeq, newPayment)
            ResponseEntity.ok(newPayment)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/delete/{paymentSeq}")
    fun deletePayment(@PathVariable paymentSeq: Int) {
        paymentService.deletePayment(paymentSeq)
    }

    @GetMapping("/list")
    fun getAllPayment(): ResponseEntity<List<Payment>> {
        val payment = paymentService.findPayment()
        return ResponseEntity.ok(payment)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getPaymentByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Payment>> {
        val payment = paymentService.findByUserSeq(userSeq)
        return ResponseEntity.ok(payment)
    }

}