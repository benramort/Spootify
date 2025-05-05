package com.deusto.theComitte.Spootify.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.SongList;

@Service
public class SongService {
    
    @Autowired
    SongRepository songRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    ArtistService artistService;
    @Autowired
    PlayListRepository playListRepository;


    public void createSong(String title, int duration, MultipartFile audioFile, long albumId, long token) throws IOException {
        Artist artist = artistService.getActiveArtist(token);
        List<Album> albums = artist.getAlbums();
        System.out.println("Albums: " + albums);
        Album album = albumRepository.findById(albumId);
        if(!albums.contains(album))
        {
            throw new RuntimeException("Album does not exist");
        }
        if (!album.getArtists().contains(artist)) {
            album.getArtists().forEach(a -> System.out.println(a.getId()));
            throw new RuntimeException("Artist does not have access to this album");
        }

        if (audioFile == null) {
            Song song = new Song(title, album, duration, "");
            album.getSongs().add(song);
            songRepository.save(song);
            return;
        }

        String fileName = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
        Path filePath = Paths.get("songs").resolve(fileName);
        Files.copy(audioFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Song song = new Song(title, album, duration, filePath.toString());
        album.getSongs().add(song);
        songRepository.save(song);
    }

    public List<Song> getSongs(long artistId, long albumId) {
        if (artistId != 0 && albumId != 0) {
            return songRepository.findByArtistIdAndAlbumId(artistId, albumId);
        }
        if (artistId != 0) {
            return songRepository.findByArtistId(artistId);
        }
        if (albumId != 0) {
            return songRepository.findByAlbumId(albumId);
        }
        return songRepository.findAll();
    }

    public List<Song> getArtistSongs(long token) {
        Artist artist = artistService.getActiveArtist(token);
        return artist.getAlbums()
            .stream()
            .flatMap(s -> s.getSongs().stream()).toList();
    }

    public Song getSong(long id) {
        Song song = songRepository.findById(id);
        if(song == null)
        {
            throw new RuntimeException("Song does not exist");
        }
        return song;
    }

    public List<Song> searchSongs(String title) {
        return songRepository.findByName(title);
    }

    public void darLike(long songId) {
        Song song = songRepository.findById(songId);
        if (song == null) {
            throw new RuntimeException("Song does not exist");
        }
        song.setNumeroLikes(song.getNumeroLikes() + 1);
        songRepository.save(song);
    }

    public Map<String, Integer> getMostLikedSongs() {
        // Recuperar todas las canciones de la base de datos
        List<Song> songs = songRepository.findAll();
    
        // Crear un mapa donde la clave es el nombre de la canción y el valor es el número de likes
        Map<String, Integer> songLikesMap = new HashMap<>();
    
        // Recorrer las canciones y añadirlas al mapa si tienen más de 0 likes
        for (Song song : songs) {
            if (song.getNumeroLikes() > 0) {
                songLikesMap.put(song.getName(), song.getNumeroLikes());
            }
        }
    
        // Ordenar el mapa de mayor a menor según el número de likes
        Map<String, Integer> sortedSongLikesMap = songLikesMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(
                        LinkedHashMap::new, // Usar LinkedHashMap para mantener el orden
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll
                );
    
        return sortedSongLikesMap;
    }

    // public Map<String, Integer> getMostLikedSongs() {
    //     String nombre = "Canciones que me gustan de";
    //     // Recuperar todas las playlists de la base de datos
    //     List<SongList> playlists = playListRepository.findAll();
    
    //     // Filtrar las playlists cuyo nombre empieza por el texto proporcionado
    //     List<SongList> matchingPlaylists = playlists.stream()
    //             .filter(playlist -> playlist.getName().startsWith(nombre))
    //             .toList();
    
    //     // Crear un mapa donde la clave es el nombre de la canción y el valor es un contador
    //     Map<String, Integer> songCountMap = new HashMap<>();
    
    //     // Recorrer las playlists coincidentes y contar las canciones
    //     for (SongList playlist : matchingPlaylists) {
    //         for (Song song : playlist.getSongs()) {
    //             songCountMap.put(song.getName(), songCountMap.getOrDefault(song.getName(), 0) + 1);
    //         }
    //     }
    
    //     // Ordenar el mapa de mayor a menor según el valor
    //     Map<String, Integer> sortedSongCountMap = songCountMap.entrySet()
    //             .stream()
    //             .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
    //             .collect(
    //                     LinkedHashMap::new, // Usar LinkedHashMap para mantener el orden
    //                     (map, entry) -> map.put(entry.getKey(), entry.getValue()),
    //                     Map::putAll
    //             );
    
    //     return sortedSongCountMap;
    // }

    // public Map<Song, Integer> getMostLikedSongs() {
    //     String nombre = "Canciones que me gustan de";
    //     // Recuperar todas las playlists de la base de datos
    //     List<SongList> playlists = playListRepository.findAll();
    
    //     // Filtrar las playlists cuyo nombre empieza por el texto proporcionado
    //     List<SongList> matchingPlaylists = playlists.stream()
    //             .filter(playlist -> playlist.getName().startsWith(nombre))
    //             .toList();
    
    //     // Crear un mapa donde la clave es la canción y el valor es un contador
    //     Map<Song, Integer> songCountMap = new HashMap<>();
    
    //     // Recorrer las playlists coincidentes y contar las canciones
    //     for (SongList playlist : matchingPlaylists) {
    //         for (Song song : playlist.getSongs()) {
    //             songCountMap.put(song, songCountMap.getOrDefault(song, 0) + 1);
    //         }
    //     }
    
    //     // Ordenar el mapa de mayor a menor según el valor
    //     Map<Song, Integer> sortedSongCountMap = songCountMap.entrySet()
    //             .stream()
    //             .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
    //             .collect(
    //                     LinkedHashMap::new, // Usar LinkedHashMap para mantener el orden
    //                     (map, entry) -> map.put(entry.getKey(), entry.getValue()),
    //                     Map::putAll
    //             );
    
    //     return sortedSongCountMap;
    // }

}
