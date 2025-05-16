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
import com.deusto.theComitte.Spootify.service.PlaylistService;

@RestController
@RequestMapping("/playlists")
@CrossOrigin(origins = "http://localhost:8080")
public class PlayListController {
    
    @Autowired
    private PlaylistService playlistService;

    @PostMapping("")
    public ResponseEntity<Void> createPlayList(@RequestBody SongListDTO songListDTO, @RequestParam long token) {
        try {
            if (songListDTO.getName() == null || songListDTO.getName().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Validación añadida
            }
            playlistService.createPlayList(token, songListDTO.getName(), songListDTO.getIsPublic());
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
            List<SongList> songLists = playlistService.getPlayLists(token);
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

    @PostMapping("/{songListId}/songs")
    public ResponseEntity<Void> addSongToPlayList(@RequestParam long token, @PathVariable long songListId, @RequestBody SongDTO song) {
        try {
            playlistService.addSongToPlayList(token, song.getId(), songListId);
            System.out.println("id: " + song.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("Playlist does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Cambiado a 404
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongListDTO> getPlaylistById(@RequestParam long token, @PathVariable Long id) {
        try {
            SongList playlist = playlistService.getPlaylistById(token, id);
            if (playlist != null) {
                return ResponseEntity.ok(playlist.toDTO());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("SongList does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (e.getMessage().equals("User does not have access to this playlist")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Void> sharePlaylist(@RequestParam long token, @PathVariable long id) {
        try {
            playlistService.sharePlaylist(id, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); 
            }
            if (e.getMessage().equals("Playlist does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
