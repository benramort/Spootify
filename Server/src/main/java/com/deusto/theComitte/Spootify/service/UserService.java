package com.deusto.theComitte.Spootify.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void createUser(String username, String email, String password) {
        System.out.println("Creating user with username: " + username + " email: " + email + " password: " + password);
    }
    
}
