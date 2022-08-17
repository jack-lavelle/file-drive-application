package com.filedriveapplication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApplicationDataJpaTests {

    @Autowired
    private FileRepository fileRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Rollback(value = false)
    void testInsertMyFile() throws IOException {
        User user1 = testInsertUserHelper("Jack", "Lavelle", "jacklavelle17@gmail.com");
        User user2 = testInsertUserHelper("sop", "doo", "sopdop@flop.com");
        User user3 = testInsertUserHelper("steve", "beve", "beve@gmail.com");

        MyFile file1 = testInsertMyFileHelper("C:\\Users\\School and Work\\Documents\\CentralFolder\\Employment\\txmq\\Coding Challenge Screens.pdf", user1);
        user1.shareFile(user2, file1);
        user1.shareFile(user3, file1);
        user1.shareFile("jacklavelle12@gmail.com", file1);
        //testInsertMyFileHelper("C:\\Users\\School and Work\\Documents\\CentralFolder\\Employment\\txmq\\Instructions.pdf", user1);
        //testInsertMyFileHelper("C:\\Users\\School and Work\\Documents\\CentralFolder\\Employment\\txmq\\log-old.docx", user1);
    }

    MyFile testInsertMyFileHelper(String path, User owner) throws IOException {
        File file = new File(path);
        MyFile myFile = new MyFile(file.getName(), file.length(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), owner, Files.readAllBytes(file.toPath()));
        MyFile savedFile = fileRepo.save(myFile);
        MyFile localFile = entityManager.find(MyFile.class, savedFile.getId());
        Assertions.assertEquals(savedFile.getSize(), localFile.getSize());

        return myFile;
    }
    User testInsertUserHelper(String first, String last, String email) throws IOException {
        User user1 = new User(first, last, email);
        User savedUser = userRepo.save(user1);
        User existUser = entityManager.find(User.class, savedUser.getId());
        Assertions.assertEquals(savedUser.getEmail(), existUser.getEmail());

        return user1;
    }
}
