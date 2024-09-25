package com.example.greening.controller

import com.example.greening.domain.item.Greening
import com.example.greening.service.GreeningService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/greening")
class GreeningController(private val greeningService: GreeningService) {

    @GetMapping("/byId/{id}")
    fun getGreeningById(@PathVariable id: Int): ResponseEntity<Greening> {
        val greening = greeningService.findOne(id)
        return if (greening != null) {
            ResponseEntity.ok(greening)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/new")
    fun createGreening(@RequestBody greening : Greening): ResponseEntity<Greening> {
        greeningService.saveGreening(greening)
        return ResponseEntity.status(HttpStatus.CREATED).body(greening)
    }

    @PutMapping("/update/{gSeq}")
    fun updateCertify(@PathVariable gSeq: Int, @RequestBody newGreening: Greening): ResponseEntity<Greening> {
        return try {
            greeningService.updateGreening(gSeq, newGreening)
            ResponseEntity.ok(newGreening)
        } catch (e: IllegalStateException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/list")
    fun getAllGreening(): ResponseEntity<List<Greening>> {
        val greening = greeningService.findGreening()
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/byGKind/{gKind}")
    fun getGreeningBygKind(@PathVariable gKind: Int): ResponseEntity<List<Greening>> {
        val greening = greeningService.findBygKind(gKind)
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/list/do")
    fun getDoGreeningBygKind(): ResponseEntity<List<Greening>> {
        val greening = greeningService.findDoGreenBygKind()
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/list/buy")
    fun getBuyGreeningBygKind(): ResponseEntity<List<Greening>> {
        val greening = greeningService.findBuyGreenBygKind()
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/list/new")
    fun getNewGreening(): ResponseEntity<List<Greening>> {
        val greening = greeningService.findNewGreen()
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/list/pop")
    fun getPopGreening(): ResponseEntity<List<Greening>> {
        val greening = greeningService.findPopGreen()
        return ResponseEntity.ok(greening)
    }

    @GetMapping("/byUserSeq/{userSeq}")
    fun getGreeningByUserSeq(@PathVariable userSeq: Int): ResponseEntity<List<Greening>> {
        val greening = greeningService.findByUserId(userSeq)
        return ResponseEntity.ok(greening)
    }

    //관리자 웹페이지
    @GetMapping("/listPage")
    fun listGreening(model: Model): String {
        val greening: List<Greening> = greeningService.findGreening()
        model.addAttribute("greenings", greening)
        return "greeningList"
    }

    @GetMapping("/edit/{gSeq}")
    fun editForm(@PathVariable gSeq: Int, model: Model): String {
        val greening = greeningService.findOne(gSeq)
        if (greening != null) {
            model.addAttribute("greening", greening)
            return "greeningEdit"
        } else {
            return "redirect:/greening/listPage"
        }
    }

    @PostMapping("/update")
    fun updateGreening(
        @ModelAttribute greening: Greening,
        redirectAttributes: RedirectAttributes
    ): String {
        greeningService.updateGreening(greening.gSeq, greening)
        redirectAttributes.addFlashAttribute("message", "그리닝이 성공적으로 업데이트되었습니다.")
        return "redirect:/greening/listPage"
    }

    @PostMapping("/delete/{gSeq}")
    fun deleteGreening(@PathVariable gSeq: Int, redirectAttributes: RedirectAttributes): String {
        greeningService.deleteGreening(gSeq)
        redirectAttributes.addFlashAttribute("message", "그리닝이 성공적으로 삭제되었습니다.")
        return "redirect:/greening/listPage"
    }
    @GetMapping("/add")
    fun showAddForm(model: Model): String {
        model.addAttribute("greening", Greening())
        return "greeningAdd"
    }

    @PostMapping("/save")
    fun saveGreening(@ModelAttribute greening: Greening, redirectAttributes: RedirectAttributes): String {
        greeningService.saveGreening(greening)
        redirectAttributes.addFlashAttribute("message", "그리닝이 성공적으로 추가되었습니다.")
        return "redirect:/greening/listPage"
    }
    @PostMapping("/save-initial")
    fun saveInitialGreening(@ModelAttribute greening: Greening): ResponseEntity<Map<String, Int>> {
        greeningService.saveGreening(greening)  
        val response = mapOf("gSeq" to greening.gSeq)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/save-final")
    fun saveFinalGreening(@ModelAttribute greening: Greening, redirectAttributes: RedirectAttributes): String {
        greeningService.updateGreening(greening.gSeq, greening)
        redirectAttributes.addFlashAttribute("message", "그리닝이 성공적으로 추가되었습니다.")
        return "redirect:/greening/listPage"
    }



}