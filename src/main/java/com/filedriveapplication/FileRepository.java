package com.filedriveapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//Repository for files, implementing two methods to find files by id and fileName.
@Repository
public interface FileRepository extends JpaRepository <MyFile, Long>{

    @Query("SELECT u FROM MyFile u WHERE u.id = ?1")
    Optional<MyFile> findByFileId(Long id);

    @Query("SELECT u FROM MyFile u WHERE u.fileName = ?1")
    Optional<MyFile> findByFileName(String fileName);

}
