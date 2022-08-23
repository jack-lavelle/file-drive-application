package com.filedriveapplication;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;

@Controller
public class AppController {

    @Autowired
    private FileRepository repo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/")
    public String viewPage(){
        return "front_page";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute("user", new User());

        return "register";
    }
    //Home page mapping.
    @GetMapping("/home_page")
    public String viewHomePage(Model model){
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Set<MyFile> ownedFiles = user.getOwnedFiles();
        model.addAttribute("ownedFiles", ownedFiles);
        return "home_page";
    }
    @GetMapping("/upload")
    public String viewUploadPage(){
        return "upload";
    }
    @PostMapping("/process_share")
    public String shareFile(Long id, String email){
        MyFile sharedFile = repo.findByFileId(id);
        User receiver = userRepo.findByEmail(email);
        User owner = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        owner.shareFile(receiver, sharedFile);
        repo.save(sharedFile);
        userRepo.save(owner);
        userRepo.save(receiver);



        return "home_page";
    }

    @PostMapping("/process_register")
    public String processRegister(User user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);

        return "front_page";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        MyFile file = new MyFile(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile.getSize(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), user, multipartFile.getBytes());
        repo.save(file);


        ra.addFlashAttribute("message", "File has been successfully uploaded.");

        return "redirect:/upload";
    }
}
