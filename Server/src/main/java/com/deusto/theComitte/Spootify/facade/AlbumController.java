package com.deusto.theComitte.Spootify.facade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.service.AlbumService;
import com.deusto.theComitte.Spootify.service.ArtistService;

@RestController
@RequestMapping("/albums")
@CrossOrigin(origins = "http://localhost:8080")
public class AlbumController {
/**
 * Esta clase se encarga de gestionar los álbumes de los artistas.
 * Permite crear álbumes, obtener álbumes de un artista y buscar álbumes por nombre.
 */

    @Autowired
    AlbumService albumService;
    @Autowired
    ArtistService artistService;

    /**
     * Crea un nuevo álbum en el artista activo
     * @param name Nombre del álbum
     * @param cover Portada del álbum. El nombre del archivo de imagen no puede tener espacios. Se acepta cualquier tipo de archivo de imagen.
     * @param token Token del artista, generado al iniciar sesión
     * @return Este método no devuelve nada
     */
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> createAlbum(
        @RequestParam("name") String name, 
        @RequestParam(value = "cover", required = false) MultipartFile cover, 
        @RequestParam long token) { //Usar albumDTO para en un futuro poder pasar más información
        try {
            String imagePath = null;
            if(cover != null && !cover.isEmpty()){

                Path imageDir = Paths.get("imagenes");
                if (!Files.exists(imageDir)) {
                    Files.createDirectories(imageDir);
                    System.out.println("Created directory: " + imageDir.toAbsolutePath());
                }

                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Path coverPath = Paths.get("imagenes").resolve(fileName);
                Files.copy(cover.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = coverPath.toString();
            }

            albumService.createAlbum(name, imagePath, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(RuntimeException e ) {
            if (e.getMessage().equals("Artist not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Devuelve la lista de álbumes de un artista
     * @param artist ID del artista
     * @return Lista de álbumes del artista
     */    
    @GetMapping("")
    public ResponseEntity<List<AlbumDTO>> getAlbums(@RequestParam(required = false, defaultValue = "0") long artist){
        try {
            List<Album> albums = albumService.getAlbums(artist);
            List<AlbumDTO> albumDTOs = new ArrayList<>();
            for(Album album : albums) {
                AlbumDTO albumDTO = album.toDTO();
                if(album.getCover() != null) {
                    String coverUrl = "http://localhost:8081/" + album.getCover();
                    albumDTO.setCover(coverUrl);
                }
                albumDTOs.add(album.toDTO());
            }
            return ResponseEntity.ok(albumDTOs);
        } catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Devuelve un álbum por su ID
     * @param id ID del álbum
     * @return Álbum correspondiente al ID dado
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbum(@PathVariable long id){
        try {
            Album album = albumService.getAlbum(id);
            AlbumDTO albumDTO = album.toDTO();
            if(album.getCover() != null) {
                String coverUrl = "http://localhost:8081/" + album.getCover();
                albumDTO.setCover(coverUrl);
            }
            return ResponseEntity.ok(albumDTO);
        } catch(RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().equals("Album not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Busca álbumes por su nombre
     * @param name Nombre del álbum
     * @return Lista de álbumes que coinciden con el nombre dado
     */
    @GetMapping("/search")
    public ResponseEntity<List<AlbumDTO>> searchAlbums(@RequestParam String name) {
        try {
            List<Album> albums = albumService.searchAlbums(name);
            List<AlbumDTO> albumDTOs = new ArrayList<>();
            for (Album album : albums) {
                albumDTOs.add(album.toDTO());
            }
            return ResponseEntity.ok(albumDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("No albums found with the given name")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}
