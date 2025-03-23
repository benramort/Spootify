package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.*;
import com.deusto.theComitte.Spootify.entity.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SongRepository songRepository;

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
    

    public void addSongToUser(long userId, List<Long> songIds, String songListName) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        for (long songId : songIds) {
            Song song = songRepository.findById(songId);
            if (song == null) {
                throw new RuntimeException("Song does not exist");
            }

            for (SongList songList : user.getSongLists()) {
                if (songList.getName().equals(songListName)) {
                    songList.getSongs().add(song);
                    songList.setUser(user);
                    return;
                }
            }
        }
    }
}
