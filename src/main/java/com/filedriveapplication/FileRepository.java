package com.filedriveapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository <MyFile, Long>{

    @Query("SELECT u FROM MyFile u WHERE u.id = ?1")
    MyFile findByFileId(Long id);

}
