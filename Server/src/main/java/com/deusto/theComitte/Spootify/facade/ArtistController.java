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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.TokenDTO;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.ArtistService;
import com.deusto.theComitte.Spootify.service.SongService;
import com.deusto.theComitte.Spootify.service.UserService;


@RestController
@RequestMapping("/artists")
@CrossOrigin(origins = "http://localhost:8080")
public class ArtistController {
/**
 * Esta clase se encarga de gestionar los artistas.
 * Permite crear artistas, iniciar sesión, cerrar sesión y obtener la lista de artistas.
 */

    @Autowired
    private ArtistService artistService;

    @Autowired
    private UserService userService;

    @Autowired
    private SongService songService;
    

    /**
     * Crea un nuevo artista en la base de datos.
     * @param artistDTO Artista a crear.
     * @return Respuesta HTTP con el estado de la operación.
     */
    @PostMapping("")
    public ResponseEntity<Void> createArtist(@RequestBody CreateUserDTO artistDTO) {
        try {
            artistService.createArtist(artistDTO.name(), artistDTO.email(), artistDTO.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e){
            e.printStackTrace();
            if(e.getMessage().equals("Artist already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            
        }
    }

    /**
     * Inicia sesión como artista.
     * @param loginDTO Datos de inicio de sesión.
     * @return Respuesta HTTP con el token de sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            long token = artistService.login(loginDTO.email(), loginDTO.password()); //No sé yo si esto me convence
            Artist artist = artistService.getActiveArtist(token);
            TokenDTO tokenDTO = new TokenDTO(artist.getId(), token);
            return ResponseEntity.ok(tokenDTO);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if(e.getMessage().equals("Incorrect password")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Cierra sesión como artista.
     * @param token Token de sesión.
     * @return Respuesta HTTP con el estado de la operación.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam long token) {
        try {
            artistService.logout(token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Devuelve la lista de canciones del artista activo.
     * @param token Token de sesión.
     * @return Respuesta HTTP con la lista de canciones.
     */
    @GetMapping("/mySongs")
    public ResponseEntity<List<SongDTO>> getMySongs(@RequestParam long token) {
        try {
            List<Song> songs = songService.getArtistSongs(token);
            List<SongDTO> songDTOs = new ArrayList<>();
            for(Song song : songs) {
                songDTOs.add(song.toDTO());
            }
            return ResponseEntity.ok(songDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Devuelve el perfil del artista activo.
     * @param token Token de sesión.
     * @return Respuesta HTTP con el perfil del artista.
     */
    @GetMapping("/myProfile")
    public ResponseEntity<ArtistDTO> getMyProfile(@RequestParam long token) {
        try {
            Artist artist = artistService.getActiveArtist(token);
            return ResponseEntity.ok(artist.toDTOWithoutAlbums());
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Devuelve una lista ocn todos los artistas.
     * @return Respuesta HTTP con la lista de artistas.
     */
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

    /**
     * Método que permite seguir a un artista.
     * @param token Token de sesión.
     * @return Respuesta HTTP con el resultado de la operación.
     */
    @PostMapping("/{artistID}/followers")
    public ResponseEntity<Void> followArtist(@RequestParam long token, @PathVariable long artistID) {
        try {
            userService.followArtist(token, artistID);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            if (ex.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (ex.getMessage().equals("Artist does not exist")){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        }
    }
    
    /**
     * Devuelve un artista por su ID.
     * @param id ID del artista.
     * @return Respuesta HTTP con el artista.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable long id) {
        try {
            Artist artist = artistService.getArtist(id);
            return ResponseEntity.ok(artist.toDTOWithFollowers());
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Artist does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    /**
     * Busca artistas por su nombre.
     * @param name Nombre del artista.
     * @return Respuesta HTTP con la lista de artistas.
     */
    @GetMapping("/search")
        public ResponseEntity<List<ArtistDTO>> searchArtists(@RequestParam String name) {
            try {
                List<Artist> artists = artistService.searchArtists(name);
                List<ArtistDTO> artistDTOs = new ArrayList<>();
                for(Artist artist : artists) {
                    artistDTOs.add(artist.toDTO());
                }
                return ResponseEntity.ok(artistDTOs);
            } catch (RuntimeException e) {
                if (e.getMessage().equals("No artists found with the given name")) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
