package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Artist;

@Service
public class ArtistService {
 
    @Autowired
    ArtistRepository artistRepository;
    
    private Map<Long, Artist> activeArtists = new HashMap<Long, Artist>();

    public void createArtist(String name, String email, String password) {
        Artist existingArtist = artistRepository.findByEmail(email);
        if(existingArtist != null) {
            throw new RuntimeException("Artist already exists");
        }
        
        Artist artist = new Artist(name, email, password);
        artistRepository.save(artist);
    }

    public Long login(String email, String password) {
        Artist artist = artistRepository.findByEmail(email);
        if(artist == null) {
            throw new RuntimeException("Artist does not exist");
        }

        if(!artist.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        long token = System.currentTimeMillis();
        activeArtists.put(token, artist);
        return token;
    }

    public void logout(long token) {
        Artist artist = activeArtists.remove(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
    }

    public List<Artist> getArtists() {
        return artistRepository.findAll();
    }

    public Map<Long, Artist> getActiveArtists() {
        return this.activeArtists;
    }    

}
