package com.filedriveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
//From Spring Security, retrieves the users authentication and authorization information.
public class AuthenticatedUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> resultUser = repo.findByEmail(email);
        User user = resultUser.get();
        if (user == null){
            throw new UsernameNotFoundException("There exists no user with that email.");
        }
        return new AuthenticatedUserDetails(user);
    }
}
