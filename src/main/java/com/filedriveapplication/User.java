package com.filedriveapplication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    //Each user will have full name and email, and the id is required by JPA.
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64) //length 64 for encryption purposes
    private String password;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<MyFile> ownedFiles;

    @ManyToMany(mappedBy = "sharedUsers", cascade = CascadeType.ALL)
    private Set<MyFile> filesSharedWithUsers = new HashSet<>();

    public void shareFile(User receiver, MyFile file){
        receiver.getOwnedFiles().add(file);
        receiver.filesSharedWithUsers.add(file);
        file.sharedUsers.add(receiver);
    }

    public void shareFile(String email, MyFile file){
        User receiver = new User("firstname", "lastname", email);
        receiver.getOwnedFiles().add(file);
        receiver.filesSharedWithUsers.add(file);
        file.sharedUsers.add(receiver);
    }

    //Constructor for user instances to be saved to the database.
    public User(String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ownedFiles = new HashSet<>();
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

    public Set<MyFile> getOwnedFiles() {
        return ownedFiles;
    }

    public void setOwnedFiles(Set<MyFile> userFiles) {
        this.ownedFiles = userFiles;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
