package com.deusto.theComitte.Spootify.facade;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
