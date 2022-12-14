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

//The main controller for handling all sorts of web requests. Also stores the repositories for files and users.
@Controller
public class AppController {

    //A note on Autowired: I have some idea what it does however what exactly a bean is I have no idea.
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
    //The previous two mappings are fairly self-explanatory. The following one takes GET request at the home_page, looks up the current user in the user repository
    //and retrieves the files they own and their name, which is then sent to the frontend for viewing.
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

    //Perhaps the most complicated method in this class. The method takes in the id of the file you want to share and the email
    //of the user you want to share it with. From here it checks if the file exists, if the user (the owner) is logged in, and if
    //there is a user associated with the email that exists, and retrieves them if possible.

    //If there was no user with that email then a default user is created with that email with placeholder information.
    //I also added flash redirect attributes to show succeed messages.
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

    //Registering new users with fail / success messages.
    @PostMapping("/process_register")
    public String processRegister(User user, RedirectAttributes ra){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Optional<User> resultUser = userRepo.findByEmail(user.getEmail());
        if (resultUser.isPresent()){
            ra.addFlashAttribute("fail", "Registered failed. There is probably already another user with that email.");
        } else {
            userRepo.save(user);
            ra.addFlashAttribute("success", "Registered successfully.");
        }


        return "redirect:/";
    }

    //Retrieves the multipart file from the front end, creating a new file and then saves that to the repo.
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws Exception {
        Optional<User> resultUser = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!resultUser.isPresent()){
            throw new Exception("Something went wrong: you are probably logged out ... please log back in.");
        }
        User user = resultUser.get();
        MyFile file = new MyFile(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile.getSize(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), user, multipartFile.getBytes());
        Optional<MyFile> resultFile = repo.findByFileName(file.getFileName());
        if (resultFile.isPresent()){
            ra.addFlashAttribute("fail", "Cannot have duplicates.");
        } else {
            repo.save(file);
            ra.addFlashAttribute("message", "File has been successfully uploaded.");
        }

        return "redirect:/home_page";
    }
    @GetMapping("/login_fail")
    public String viewLoginFail(RedirectAttributes ra){
        ra.addFlashAttribute("message", "Login Failed: User does not exist or password was wrong. Try again.");
        return "redirect:/";
    }

    //Download functionality.
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

    //The controller for viewing your shared files. The user is retrieved, then a query is made to the joined table that
    //shows all currently shared files, and if a file is owned by the current user and in that joined table it will
    //be added to sharedFiles and then sent to the frontend.
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

    //Very similar to the previous controller except the logic of query is slightly different to retrieve all files
    //shared with the current user rather than all files they are sharing.
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
