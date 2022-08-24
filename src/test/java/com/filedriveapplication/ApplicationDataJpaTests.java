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
import java.util.Optional;

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
    void testDriver() throws IOException {
        User user1 = testInsertUserHelper("jack", "lavel", "j22@gmail.com", "apples");
        User user2 = testInsertUserHelper("d", "l", "d22@gmaikl.com", "sapples");
        MyFile myFile = testInsertMyFileHelper("C:\\Users\\School and Work\\Documents\\CentralFolder\\Employment\\txmq\\Instructions.pdf", user1);
        user1.shareFile(user2, myFile);
    }

    void testFindByEmailHelper(String email){
        Optional<User> resultUser = userRepo.findByEmail(email);
        User user = resultUser.get();

        Assertions.assertNotNull(user);
    }
    MyFile testInsertMyFileHelper(String path, User owner) throws IOException {
        File file = new File(path);
        MyFile myFile = new MyFile(file.getName(), file.length(), Date.valueOf(LocalDate.now(ZoneId.of("America/Montreal"))), owner, Files.readAllBytes(file.toPath()));
        MyFile savedFile = fileRepo.save(myFile);
        MyFile localFile = entityManager.find(MyFile.class, savedFile.getId());
        Assertions.assertEquals(savedFile.getSize(), localFile.getSize());

        return myFile;
    }
    User testInsertUserHelper(String first, String last, String email, String password) throws IOException {
        User user1 = new User(first, email, password);
        User savedUser = userRepo.save(user1);
        User existUser = entityManager.find(User.class, savedUser.getId());
        Assertions.assertEquals(savedUser.getEmail(), existUser.getEmail());

        return user1;
    }
}
