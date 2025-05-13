package com.clothingstore.clothing_store_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping("/test")
    public String testAuth() {
        return "Authenticated!";
    }
}
