package com.example.greening.controller

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class AddressController {

    @GetMapping("/address")
    fun getAddressPage(): String {
        return "address"
    }
}