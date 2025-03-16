package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {

    @GetMapping("/songs")
    public ResponseEntity<List<SongDTO>> GetSongs() {
        Artist artist = new Artist(1, "Artist", "artist@art", "1234");
        Song song = new Song(1, "Song", artist, 100, "https://www.youtube.com");
        List<SongDTO> songs = new ArrayList<>();
        songs.add(song.toDTO());
        songs.add(song.toDTO());
        return ResponseEntity.ok(songs);
    }
    
}
