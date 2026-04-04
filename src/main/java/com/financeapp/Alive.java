package com.financeapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alive")
public class Alive {

    @GetMapping
    public String checkAlive(){
        return "Backend is live...";
    }

}
