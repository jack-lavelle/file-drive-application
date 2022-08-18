package com.filedriveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private UserRepository userRepo;

    @GetMapping("")
    public String viewPage(){
        return "front_page";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute("user", new User());

        return "register";
    }
    //Home page mapping.
    @GetMapping("/home")
    public String viewHomePage(){

        return "upload";
    }
    @GetMapping("/upload")
    public String viewUploadPage(){
        return "upload";
    }

    @PostMapping("/process_register")
    public String processRegister(User user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);

        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
        AuthenticatedUserDetails authUser= (AuthenticatedUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepo.findByEmail(authUser.getUsername());
        System.out.println(user.getEmail());
        MyFile file = new MyFile(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile.getSize(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), user, multipartFile.getBytes());
        repo.save(file);


        ra.addFlashAttribute("message", "success");

        return "redirect:/upload";
    }
}
