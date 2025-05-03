package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public void createArtist(String name, String email, String password) {
        Artist existingArtist = artistRepository.findByEmail(email);
        if(existingArtist != null) {
            throw new RuntimeException("Artist already exists");
        }
        
        Artist artist = new Artist(name, email, password);
        artistRepository.save(artist);
    }

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

    public void logout(long token) {
        Artist artist = activeArtists.remove(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
    }

    public List<Artist> getArtists() {
        return artistRepository.findAll();
    }
    
    public Artist getArtist(long id) {
        Artist artist = artistRepository.findById(id);
        if(artist == null) {
            throw new RuntimeException("Artist does not exist");
        }
        return artist;
    }

    public Artist getActiveArtist(long token) {
        Artist artist = activeArtists.get(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        return artist;
    }

    public Map<Long, Artist> getActiveArtistMap(){
        return activeArtists;
    }

    public List<Artist> searchArtists(String name) {
        return artistRepository.findByName(name);
    }

    public Map<String, Integer> getMostFollowedArtists() {
        // Recuperar todos los artistas de la base de datos
        List<Artist> artists = artistRepository.findAll();
    
        // Crear un mapa donde la clave es el nombre del artista y el valor es el número de seguidores
        Map<String, Integer> artistFollowerCountMap = new HashMap<>();
    
        // Recorrer los artistas y contar el tamaño de su lista de seguidores
        for (Artist artist : artists) {
            artistFollowerCountMap.put(artist.getName(), artist.getFollowersList().size());
        }
    
        // Ordenar el mapa de mayor a menor según el número de seguidores
        Map<String, Integer> sortedArtistFollowerCountMap = artistFollowerCountMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(
                        LinkedHashMap::new, // Usar LinkedHashMap para mantener el orden
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll
                );
    
        return sortedArtistFollowerCountMap;
    }

    // public Map<Artist, Integer> getMostFollowedArtists2() {
    //     // Recuperar todos los artistas de la base de datos
    //     List<Artist> artists = artistRepository.findAll();
    
    //     // Crear un mapa donde la clave es el artista y el valor es el número de seguidores
    //     Map<Artist, Integer> artistFollowerCountMap = new HashMap<>();
    
    //     // Recorrer los artistas y contar el tamaño de su lista de seguidores
    //     for (Artist artist : artists) {
    //         artistFollowerCountMap.put(artist, artist.getFollowersList().size());
    //     }
    
    //     // Ordenar el mapa de mayor a menor según el número de seguidores
    //     Map<Artist, Integer> sortedArtistFollowerCountMap = artistFollowerCountMap.entrySet()
    //             .stream()
    //             .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
    //             .collect(
    //                     LinkedHashMap::new, // Usar LinkedHashMap para mantener el orden
    //                     (map, entry) -> map.put(entry.getKey(), entry.getValue()),
    //                     Map::putAll
    //             );
    
    //     return sortedArtistFollowerCountMap;
    // }
}
