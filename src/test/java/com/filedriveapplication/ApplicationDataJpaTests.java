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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApplicationDataJpaTests {

    @Autowired
    private FileRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Rollback(value = false)
    void testInsertMyFile() throws IOException {
        String path = "C:\\Users\\School and Work\\Documents\\CentralFolder\\Employment\\txmq\\Coding Challenge Screens.pdf";
        File file = new File(path);
        MyFile myFile = new MyFile(file.getName(), file.length(), file.lastModified(), Files.readAllBytes(file.toPath()));

        MyFile savedFile = repo.save(myFile);
        MyFile localFile = entityManager.find(MyFile.class, savedFile.getId());

        Assertions.assertEquals(savedFile.getSize(), localFile.getSize());
    }
}