package com.filedriveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@Controller
public class AppController {

    @Autowired
    private FileRepository repo;
    //Home page mapping.
    @GetMapping("/")
    public String viewHomePage(){

        return "home";
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws IOException {

        User me = new User("jack", "lavelle", "jacklavelle17@gmail.com");
        MyFile file = new MyFile(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile.getSize(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), me, multipartFile.getBytes());

        repo.save(file);

        ra.addFlashAttribute("message", "success");

        return "redirect:/";
    }
}
