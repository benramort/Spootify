package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Artist;

@Service
public class ArtistService {
 
    @Autowired
    ArtistRepository artistRepository;
    
    private Map<Long, Artist> activeArtists = new HashMap<Long, Artist>();

    /**
     * Crea un nuevo artista en la base de datos.
     * @param name Nombre del artista
     * @param email Correo electrónico del artista
     * @param password Contraseña del artista
     * @throws RuntimeException Si el artista ya existe. Se comprueba con el email
     */
    public void createArtist(String name, String email, String password) {
        Artist existingArtist = artistRepository.findByEmail(email);
        if(existingArtist != null) {
            throw new RuntimeException("Artist already exists");
        }
        
        Artist artist = new Artist(name, email, password);
        artistRepository.save(artist);
    }

    /**
     * Inicia sesión como artista.
     * @param email Correo electrónico del artista
     * @param password Contraseña del artista
     * @return Token de sesión
     * @throws RuntimeException Si el artista no existe o la contraseña es incorrecta
     */
    public Long login(String email, String password) {
        Artist artist = artistRepository.findByEmail(email);
        if(artist == null) {
            throw new RuntimeException("Artist does not exist");
        }

        if(!artist.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        long token = System.currentTimeMillis();
        activeArtists.put(token, artist);
        return token;
    }

    /**
     * Cierra sesión como artista.
     * @param token Token de sesión
     * @throws RuntimeException Si el artista no está loggeado
     * @return Este método no devuelve nada
     */
    public void logout(long token) {
        Artist artist = activeArtists.remove(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
    }

    /**
     * Devuelve la lista de artistas.
     * @return Lista de artistas
     */
    public List<Artist> getArtists() {
        return artistRepository.findAll();
    }
    
    /**
     * Devuelve un artista por su ID.
     * @param id ID del artista
     * @return Artista correspondiente al ID dado
     * @throws RuntimeException Si el artista no existe
     */
    public Artist getArtist(long id) {
        Artist artist = artistRepository.findById(id);
        if(artist == null) {
            throw new RuntimeException("Artist does not exist");
        }
        return artist;
    }

    /**
     * Devuelve el artista activo por su token.
     * @param token Token de sesión
     * @return Artista correspondiente al token dado
     * @throws RuntimeException Si el artista no está loggeado
     */
    public Artist getActiveArtist(long token) {
        Artist artist = activeArtists.get(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        return artist;
    }

    /**
     * Devuelve el mapa de artistas activos.
     * @return Mapa de artistas activos
     */
    public Map<Long, Artist> getActiveArtistMap(){
        return activeArtists;
    }

    /**
     * Busca artistas por su nombre.
     * @param name Nombre del artista
     * @return Lista de artistas que coinciden con el nombre dado
     */
    public List<Artist> searchArtists(String name) {
        return artistRepository.findByName(name);
    }
}
