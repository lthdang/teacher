package com.teachermanagement.teacher_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/hello")
     public ResponseEntity<String> test() {
        return ResponseEntity
                .ok("Test API Successfully");
    }
    
}
