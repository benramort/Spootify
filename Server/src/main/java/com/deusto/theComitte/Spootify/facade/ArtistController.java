package com.deusto.theComitte.Spootify.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.service.ArtistService;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;
    
    @PostMapping("")
    public ResponseEntity<Void> createArtist(@RequestBody CreateUserDTO artistDTO) {
        try {
            artistService.createArtist(artistDTO.name(), artistDTO.email(), artistDTO.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e){
            if(e.getMessage().equals("Artist already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginDTO loginDTO) {
        try {
           long token = artistService.login(loginDTO.email(), loginDTO.password());
           return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if(e.getMessage().equals("Incorrect password")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam long token) {
        try {
            artistService.logout(token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<ArtistDTO>> getArtists() {
        try {
            List<Artist> artists = artistService.getArtists();
            List<ArtistDTO> artistDTOs = new ArrayList<>();
            for(Artist artist : artists) {
                artistDTOs.add(artist.toDTO());
            }
            return ResponseEntity.ok(artistDTOs);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
