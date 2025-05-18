package com.deusto.theComitte.Spootify.facade;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.SongService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    /**
     * Obtiene una lista de canciones, filtrando opcionalmente por artista o álbum.
     *
     * @param artistId ID del artista (opcional, por defecto 0)
     * @param albumId ID del álbum (opcional, por defecto 0)
     * @return Lista de SongDTO con las canciones encontradas o BAD_REQUEST si ocurre un error
     */
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

    /**
     * Crea una nueva canción asociada a un álbum.
     *
     * @param title Título de la canción
     * @param albumId ID del álbum al que pertenece la canción
     * @param duration Duración de la canción en segundos
     * @param audioFile Archivo de audio de la canción (opcional)
     * @param token Token de autenticación del artista
     * @return OK si la canción se crea correctamente, NOT_FOUND si el artista no está logueado o el álbum no existe, BAD_REQUEST en otros errores
     */
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

    /**
     * Busca canciones por título.
     *
     * @param title Título o parte del título de la canción a buscar
     * @return Lista de SongDTO que coinciden con el título o BAD_REQUEST si ocurre un error
     */
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

    /**
     * Permite a un usuario dar like a una canción.
     *
     * @param songId ID de la canción a la que se da like
     * @param token Token de autenticación del usuario
     * @return OK si el like se registra correctamente, UNAUTHORIZED si el usuario no está logueado, NOT_FOUND si la canción no existe, BAD_REQUEST en otros errores
     */
    @PostMapping("/like")
    public ResponseEntity<Void> likeSong(@RequestParam long songId, @RequestParam long token) {
        try {
            songService.darLike(songId, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("Song does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene la lista de canciones con más likes.
     *
     * @return Lista de SongDTO de las canciones más gustadas o BAD_REQUEST si ocurre un error
     */
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
