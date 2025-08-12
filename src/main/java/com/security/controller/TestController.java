package com.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // ✅ Unsecured endpoint (open to all)
    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint. No authentication required.";
    }

    // 🔐 Secured endpoint (requires valid JWT)
    @GetMapping("/secure")
    public String secureEndpoint() {
        return "This is a secured endpoint. You are authenticated!";
    }
}
