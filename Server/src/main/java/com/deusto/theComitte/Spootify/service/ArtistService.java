package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;

@Service
public class ArtistService {
 
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    SongRepository songRepository;

    Map<Long, Artist> activeArtists = new HashMap<Long, Artist>();

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

    public void postSong(long songId, String songTitle, int songDuration, String youtubeURL, long token) {
        Artist artist = activeArtists.get(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        Song song = new Song(songId, songTitle, artist, songDuration, youtubeURL);
        songRepository.save(song);
        artist.getSongs().add(song);
        artistRepository.save(artist);

    }

}
