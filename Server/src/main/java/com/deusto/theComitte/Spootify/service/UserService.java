package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    private Map<Long, User> activeUsers = new HashMap<>();

    public void createUser(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }
        
        User user = new User(name, email, password);
        userRepository.save(user);
    }

    public long login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }
        long token = System.currentTimeMillis();
        activeUsers.put(token, user);
        return token;
    }

    public void logout(long token) {
        User user = activeUsers.remove(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getActiveUser(long token) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return user;
    }
    
    public void followArtist(long token, long artistID) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Artist artist = artistRepository.findById(artistID);
        if (artist == null) {
            throw new RuntimeException("Artist does not exist");
        }
        artist.followArtist(user);
        userRepository.save(user);
        artistRepository.save(artist);
    }	
     

    
}
