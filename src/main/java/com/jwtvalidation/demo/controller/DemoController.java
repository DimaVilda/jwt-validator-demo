package com.jwtvalidation.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        return new ResponseEntity<>("JWT token authenticated, return 200 response", HttpStatus.OK);
    }
}
