package com.filedriveapplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    //Home page mapping.
    @GetMapping("/")
    public String viewHomePage(){

        return "home";
    }
}
