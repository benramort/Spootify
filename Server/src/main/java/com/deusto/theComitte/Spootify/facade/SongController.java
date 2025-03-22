package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.service.SongService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    SongService songService;

    @GetMapping()
    public ResponseEntity<List<SongDTO>> getSongs(
        @RequestParam(required = false, defaultValue = "0") long artistId,
        @RequestParam(required = false, defaultValue = "0") long albumId
    ) {
        try {
            songService.getSongs(artistId, albumId);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping()
    public ResponseEntity<Void> createSong(@RequestBody SongDTO songDTO, @RequestParam long token) {
        try {
            songService.createSong(songDTO.getTitle(), songDTO.getDuration(), songDTO.getYoutubeUrl(), songDTO.getAlbum().getId(), token);
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
