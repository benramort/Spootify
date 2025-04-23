package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.SongService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@CrossOrigin(origins = "http://localhost:8080")
public class SongController {

    @Autowired
    SongService songService;

    @GetMapping("")
    public ResponseEntity<List<SongDTO>> getSongs(
        @RequestParam(required = false, defaultValue = "0") long artistId,
        @RequestParam(required = false, defaultValue = "0") long albumId
    ) {
        try {
            List<Song> songs = songService.getSongs(artistId, albumId);
            List<SongDTO> songDTOs = new ArrayList<>();
            for (Song song : songs) {
                songDTOs.add(song.toDTO());
            }
            return ResponseEntity.ok(songDTOs);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    @PostMapping("")
    public ResponseEntity<Void> createSong(@RequestBody SongDTO songDTO, @RequestParam long token) {
        try {
            songService.createSong(songDTO.getTitle(), songDTO.getDuration(), songDTO.getYoutubeUrl(), songDTO.getAlbum().getId(), token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (e.getMessage().equals("Album does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        try {
            List<Song> songs = songService.searchSongs(title);
            List<SongDTO> songDTOs = new ArrayList<>();
            for (Song song : songs) {
                songDTOs.add(song.toDTO());
            }
            System.out.println("Canciones sacadas por query de controller:"+ songDTOs.toString());

            return ResponseEntity.ok(songDTOs);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    
}
