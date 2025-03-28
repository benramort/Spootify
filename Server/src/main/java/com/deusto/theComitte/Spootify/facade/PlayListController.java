package com.deusto.theComitte.Spootify.facade;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.service.UserService;

@RestController
@RequestMapping("/playlists")
@CrossOrigin(origins = "http://localhost:8080")
public class PlayListController {
    
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<Void> createPlayList(@RequestBody SongListDTO songListDTO, @RequestParam long token) {
        try {
            userService.createPlayList(token, songListDTO.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }   
    }

    @GetMapping("")
    public ResponseEntity<List<SongListDTO>> getPlayLists(@RequestParam long token) {
        try {
            List<SongListDTO> songListDTOs = new ArrayList<SongListDTO>();
            List<SongList> songLists = userService.getPlayLists(token);
            for (SongList songList : songLists) {
                songListDTOs.add(songList.toDTO());
            }
            return ResponseEntity.ok(songListDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // @PostMapping("/{songListId}/songs")
    // public ResponseEntity<Void> addSongToPlayList(@RequestParam long token, @PathVariable long songListId, @RequestBody SongRequests songIds) {
    //     try {
    //         for (long songId : songIds.getSongIds()) {
    //             userService.addSongToPlayList(token, songId, songListId);
    //         }
    //         return new ResponseEntity<>(HttpStatus.OK);
    //     } catch (RuntimeException e) {
    //         if (e.getMessage().equals("User not logged in")) {
    //             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    //         }
    //         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //     }
    // }

    @PostMapping("/{songListId}/songs")
    public ResponseEntity<Void> addSongToPlayList(@RequestParam long token, @PathVariable long songListId, @RequestBody SongDTO song) {
        try {
            userService.addSongToPlayList(token, song.getId(), songListId);
            System.out.println("id: " + song.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
