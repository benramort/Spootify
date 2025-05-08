package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    PlayListRepository songListRepository;

    private Map<Long, User> activeUsers = new HashMap<>();

    public void createUser(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }
        
        // First save the user (without the song list reference)
        User user = new User(name, email, password);
        userRepository.save(user);
        
        // Then create and save the song list
        String nombre = "Canciones que me gustan";
        SongList cancionesQueMeGustan = new SongList(nombre, user);
        songListRepository.save(cancionesQueMeGustan);
        
        // Now that the song list has an ID, set it on the user and update
        user.addSongList(cancionesQueMeGustan);
        user.setCancionesMeGustanID(cancionesQueMeGustan.getId());
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
        System.out.println("Token: " + token);
        // System.out.println("Active users: " + activeUsers);
        User user = activeUsers.get(token);
        // System.out.println("User: " + user);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return user;
    }

    public Map<Long, User> getActiveUserMap() {
        return this.activeUsers;
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

    public SongList getLikedSongs(long token) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        SongList songList = songListRepository.findById(user.getCancionesMeGustanID());
        if (songList == null) {
            throw new RuntimeException("Song list does not exist");
        }
        return songList;
    }	
     

    
}
