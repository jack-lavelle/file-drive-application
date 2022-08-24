package com.filedriveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
public class AppController {

    @Autowired
    private FileRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EntityManager em;

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
    public String viewHomePage(Model model) throws Exception {
        Optional<User> result = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!result.isPresent()){
            throw new Exception("User does not exist.");
        }
        User user = result.get();
        Set<MyFile> ownedFiles = user.getOwnedFiles();
        model.addAttribute("ownedFiles", ownedFiles);
        model.addAttribute("name", user.getName());
        return "home_page";
    }
    @GetMapping("/upload")
    public String viewUploadPage(){
        return "upload";
    }
    @PostMapping("/process_share")
    public String shareFile(Long id, String email, RedirectAttributes ra) throws Exception {
        Optional<MyFile> result = repo.findByFileId(id);
        if (!result.isPresent()){
            throw new Exception("Could not find file with id: " + id);
        }
        MyFile sharedFile = result.get();
        Optional<User> resultReceiver = userRepo.findByEmail(email);
        Optional<User> resultOwner = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!resultOwner.isPresent()){
            throw new Exception("Something went wrong: you are probably logged out ... please log back in.");
        }
        User owner = resultOwner.get();

        User receiver;
        if (!resultReceiver.isPresent()){
            receiver = owner.shareFile(email, sharedFile);
            userRepo.save(receiver);
        } else {
            receiver = resultReceiver.get();
            owner.shareFile(receiver, sharedFile);
        }
        owner.getFilesSharedWithUsers().add(sharedFile);
        repo.save(sharedFile);
        userRepo.save(owner);
        userRepo.save(receiver);
        ra.addFlashAttribute("message", "File successfully shared");
        return "redirect:/home_page";
    }

    @PostMapping("/process_register")
    public String processRegister(User user, RedirectAttributes ra){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        ra.addFlashAttribute("success", "Registered successfully.");

        return "redirect:/";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws Exception {
        Optional<User> resultUser = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!resultUser.isPresent()){
            throw new Exception("Something went wrong: you are probably logged out ... please log back in.");
        }
        User user = resultUser.get();
        MyFile file = new MyFile(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile.getSize(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), user, multipartFile.getBytes());
        repo.save(file);


        ra.addFlashAttribute("message", "File has been successfully uploaded.");

        return "redirect:/home_page";
    }
    @GetMapping("/login_fail")
    public String viewLoginFail(RedirectAttributes ra){
        ra.addFlashAttribute("message", "Login Failed: User does not exist or password was wrong. Try again.");
        return "redirect:/";
    }

    @GetMapping("/download")
    public void downloadFile(@Param("id") Long id, HttpServletResponse response) throws Exception {
        Optional<MyFile> result = repo.findByFileId(id);
        if (!result.isPresent()){
            throw new Exception("Could not find file with id: " + id);
        }
        MyFile myFile = result.get();

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + myFile.getFileName();
        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(myFile.getContent());
        outputStream.close();
    }
    @GetMapping("/delete")
    public String deleteFile(@Param("id") Long id) throws Exception {
        Optional<MyFile> result = repo.findByFileId(id);
        if (!result.isPresent()){
            throw new Exception("Could not find file with id: " + id);
        }
        repo.delete(result.get());

        return "redirect:/home_page";
    }

    @GetMapping("/shared_files")
    public String viewSharedFiles(Model model) throws Exception {
        Optional<User> resultUser = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!resultUser.isPresent()){
            throw new Exception("Something went wrong: you are probably logged out ... please log back in.");
        }
        User user = resultUser.get();
        Query mySet = em.createNativeQuery("SELECT DISTINCT *\n" +
                "FROM files.files\n" +
                "WHERE files.file_id in (SELECT file_id FROM my_file_user) and files.user_id =" + String.valueOf(user.getId()) +
                ";", MyFile.class);

        List<MyFile> sharedFiles = mySet.getResultList();
        model.addAttribute("sharedFiles", sharedFiles);

        return "shared_files";
    }

    @GetMapping("/shared_files_with_me")
    public String viewShareFiles(Model model) throws Exception {
        Optional<User> resultUser = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!resultUser.isPresent()){
            throw new Exception("Something went wrong: you are probably logged out ... please log back in.");
        }
        User user = resultUser.get();
        Query mySet = em.createNativeQuery("SELECT DISTINCT *\n" +
                "FROM files.files\n" +
                "WHERE user_id in (SELECT user_id FROM my_file_user) and user_id !=" + user.getId() + ";", MyFile.class);

        List<MyFile> shareFiles = mySet.getResultList();
        model.addAttribute("shareFiles", shareFiles);

        return "shared_files_with_me";
    }
}
