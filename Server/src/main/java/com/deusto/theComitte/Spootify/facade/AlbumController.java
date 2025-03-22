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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.service.AlbumService;
import com.deusto.theComitte.Spootify.service.ArtistService;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    AlbumService albumService;
    @Autowired
    ArtistService artistService;

    @PostMapping("")
    public ResponseEntity<Void> createAlbum(@RequestBody AlbumDTO albumDTO, @RequestParam long token) { //Usar albumDTO para en un futuro poder pasar más información
        try {
            albumService.createAlbum(albumDTO.getName(), token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(RuntimeException e){
            e.printStackTrace();
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<AlbumDTO>> getAlbums() {
        try {
            List<Album> albums = albumService.getAlbums();
            List<AlbumDTO> albumDTOs = new ArrayList<>();
            for(Album album : albums) {
                albumDTOs.add(album.toDtoWithLists());
            }
            return ResponseEntity.ok(albumDTOs);
        } catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
