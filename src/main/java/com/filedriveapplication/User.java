package com.filedriveapplication;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    //Each user will have full name and email, and the id is required by JPA.
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<MyFile> userFiles = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private MyFile files;
    //Constructor for user instances to be saved to the database.
    public User(String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    //A no arg constructor is required by JPA and is protected since you do not use it directly.
    protected User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<MyFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(Set<MyFile> userFiles) {
        this.userFiles = userFiles;
    }

    public MyFile getFiles() {
        return files;
    }

    public void setFiles(MyFile files) {
        this.files = files;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
