package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.ArtistService;
import com.deusto.theComitte.Spootify.service.SongService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {

    @Autowired
    SongService songService;

    @GetMapping("/songs")
    public ResponseEntity<List<SongDTO>> getSongs() {
        Artist artist = new Artist(1, "Artist", "artist@art", "1234");
        Song song = new Song(1, "Song", artist.getAlbums().get(0), 100, "https://www.youtube.com");
        List<SongDTO> songs = new ArrayList<>();
        songs.add(song.toDTO());
        songs.add(song.toDTO());
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/songs")
    public ResponseEntity<Void> createSong(@RequestBody SongDTO songDTO, @RequestParam long token) {
        try {
            songService.createSong(songDTO.getId(), songDTO.getTitle(), songDTO.getDuration(), songDTO.getYoutubeUrl(), songDTO.getAlbum().getId(), token);
            System.out.println("YT:" + songDTO.getYoutubeUrl());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            System.out.println(e.getMessage());
            System.out.println("YT:" + songDTO.getYoutubeUrl());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}
