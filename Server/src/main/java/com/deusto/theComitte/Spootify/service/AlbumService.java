package com.deusto.theComitte.Spootify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;

@Service
public class AlbumService {
    
    ArtistService artistService;
    ArtistRepository artistRepository;
    AlbumRepository albumRepository;

    @Autowired
    public AlbumService(ArtistService artistService, ArtistRepository artistRepository, AlbumRepository albumRepository) {
        this.artistService = artistService;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
    }

    /**
     * Crea un nuevo álbum en el artista activo
     * @param name Nombre del álbum
     * @param cover Portada del álbum
     * @param token Token del artista, generado al iniciar sesión
     * @return Este método no devuelve nada
     */
    public void createAlbum(String name, String cover, long token) {
        Artist artist = artistService.getActiveArtist(token);
        Album album = new Album(name);
        album.setCover(cover);
        artist.getAlbums().add(album);
        album.getArtists().add(artist);
        albumRepository.save(album);
        artistRepository.save(artist);
    }

    /**
     * Devuelve la lista de álbumes de un artista
     * @param artistId ID del artista
     * @return Lista de álbumes del artista
     */
    public List<Album> getAlbums(long artistId) {
        if(artistId != 0) {
            Artist artist = artistRepository.findById(artistId);
            return artist.getAlbums();
        }
        return albumRepository.findAll();
    }

    /**
     * Devuelve un álbum por su ID
     * @param id ID del álbum
     * @return Álbum correspondiente al ID dado
     * @throws RuntimeException Si no se encuentra el álbum.
     */
    public Album getAlbum(long id) {
        Album album = albumRepository.findById(id);
        if (album == null) {
            throw new RuntimeException("Album not found");
        }
        return album;
    }

    /**
     * Busca álbumes por su nombre
     * @param name Nombre del álbum
     * @return Lista de álbumes que coinciden con el nombre dado
     * @throws RuntimeException Si no se encuentran álbumes con el nombre dado
     */
    public List<Album> searchAlbums(String name) {
        List<Album> albums = albumRepository.findByName(name);
        if (albums.isEmpty()) {
            throw new RuntimeException("No albums found with the given name");
        }
        return albums;
    }

    /**
     * Devuelve la lista de álbumes de un artista
     * @param artistId ID del artista
     * @return Lista de álbumes del artista
     * @throws RuntimeException Si el artista no está loggeado
     */
    public List<Album> getArtistAlbums(long artistId) {
        Artist artist = artistService.getActiveArtist(artistId);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        return artist.getAlbums();
        //return albumRepository.findByArtistId(artist.getId());
    }

    /**
     * Devuelve todos los álbumes
     * @return Lista de todos los álbumes
     */
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }
}
