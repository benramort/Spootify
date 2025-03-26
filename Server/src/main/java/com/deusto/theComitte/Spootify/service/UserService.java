package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    PlayListRepository songListRepository;

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
    
    public void followArtist(long token, long artistID) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Artist artist = artistRepository.findById(artistID);
        if (artist == null) {
            throw new RuntimeException("Artist does not exist");
        }
        user.getFollowList().add(artist);
        userRepository.save(user);
        artist.setFollowers(artist.getFollowers() + 1);
        artistRepository.save(artist);
    }	
     

    public void addSongsToUser(long userId, List<Long> songIds, long songListId) {
        User user = activeUsers.get(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        for (long songId : songIds) {
            Song song = songRepository.findById(songId);
            if (song == null) {
                throw new RuntimeException("Song does not exist");
            }

            for (SongList songList : user.getSongLists()) {
                if (songList.getId().equals(songListId)) {
                    songList.getSongs().add(song);
                    songList.setUser(user);
                    return;
                }
            }
        }
    }

    public void addSongToPlayList(long userId, long songId, long songListId) {
        User user = activeUsers.get(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        Song song = songRepository.findById(songId);
        if (song == null) {
            throw new RuntimeException("Song does not exist");
        }

        SongList songList = songListRepository.findById(songListId);
        if (songList == null) {
            throw new RuntimeException("SongList does not exist");
        }
        songList.getSongs().add(song);
        songListRepository.save(songList);
        
    }

    public void createPlayList(long userId, String name) {
        User user = activeUsers.get(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        user.createSongList(name);
        userRepository.save(user);
    }

    public List<SongList> getPlayLists(long userId) {
        User user = activeUsers.get(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return user.getSongLists();
    }
}
