package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.SongService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> createSong(
        @RequestParam("title") String title,
        @RequestParam("album") long albumId,
        @RequestParam("duration") int duration,
        @RequestParam(value = "file", required = false) MultipartFile audioFile,
        @RequestParam("token") long token) {
        try {
            songService.createSong(title, duration, audioFile, albumId, token);
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
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        try {
            System.out.println("Title received in controller: " + title);
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

    @PostMapping("/like")
    public ResponseEntity<Void> likeSong(@RequestParam long songId) {
        try {
            songService.darLike(songId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("Song does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // @GetMapping("/mostlikedsongs")
    // public ResponseEntity<Map<String, Integer>> getMostLikedSongs() {
    //     try {
    //         Map<String, Integer> mostLikedSongs = songService.getMostLikedSongs();
    //         return ResponseEntity.ok(mostLikedSongs);
    //     } catch (RuntimeException e) {
    //         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //     }
    // }

    @GetMapping("/mostlikedsongs")
    public ResponseEntity<List<SongDTO>> getMostLikedSongs() {
        try {
            // Obtener la lista de canciones más gustadas
            List<Song> mostLikedSongs = songService.getMostLikedSongs();

            // Convertir la lista de canciones a una lista de SongDTO
            List<SongDTO> mostLikedSongsDTO = new ArrayList<>();
            for (Song song : mostLikedSongs) {
                mostLikedSongsDTO.add(song.toDTO());
            }

            return ResponseEntity.ok(mostLikedSongsDTO);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
