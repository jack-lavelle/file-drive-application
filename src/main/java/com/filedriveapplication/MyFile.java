package com.filedriveapplication;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "files")
public class MyFile {

    //The entity class for files. This is where I specified what a file is, what columns it will have in the database,
    //and various sets corresponding to different things within the files. This page is similar to the User class as
    //well. All in all, nothing much going on besides: 1) setting the id for each file so MySQL can function, 2)
    //setting what the parameters for a file are, 3) mapping the relations between files and users so users can own
    //files, or files can be shared, or allowing for various queries to be made. The rest is just setters and getters.
    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    private long size;
    @Column(name = "upload_Date")
    private Date uploadDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //Many files to one owner.
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //One file to many shared users
    @JoinTable(
            name = "MyFile_User",
            joinColumns = {@JoinColumn(name = "file_id")},
            inverseJoinColumns = {@JoinColumn(name = "shared_user_id")}
    )
    Set<User> sharedUsers = new HashSet<>();

    @Lob
    private byte[] content;

    //Constructors (protected for JPA and public for database).
    public MyFile(String fileName, long size, Date uploadDate, User owner, byte[] content){
        this.fileName = fileName;
        this.size = size;
        this.uploadDate = uploadDate;
        this.owner = owner;
        this.sharedUsers = new HashSet<>();
        this.content = content;
    }
    protected MyFile(){}

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

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
