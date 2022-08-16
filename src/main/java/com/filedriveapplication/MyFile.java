package com.filedriveapplication;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Date;

@Entity
@Table(name = "files")
public class MyFile {

    //Each file has a name, a size, day of upload, its content, and then the Id as required by JPA.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String fileName;
    private long size;
    @Column(name = "upload_Date")
    private Date uploadDate;

    @Lob
    private byte[] content;

    //Constructors (protected for JPA and public for database).
    public MyFile(String fileName, long size, Date uploadDate, byte[] content){
        this.fileName = fileName;
        this.size = size;
        this.uploadDate = uploadDate;
        this.content = content;
    }
    protected MyFile(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
