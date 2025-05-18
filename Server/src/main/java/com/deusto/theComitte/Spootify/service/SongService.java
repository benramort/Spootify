package com.deusto.theComitte.Spootify.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class SongService {
    
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private ArtistService artistService;
    private PlaylistService playlistService;
    private UserService userService;

    @Autowired
    public SongService(SongRepository songRepository, AlbumRepository albumRepository,
            ArtistService artistService, PlaylistService playlistService, UserService userService) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.playlistService = playlistService;
        this.userService = userService;
    }


    /**
     * Crea una nueva canción asociada a un álbum y la guarda en la base de datos.
     *
     * @param title Título de la canción
     * @param duration Duración de la canción en segundos
     * @param audioFile Archivo de audio de la canción (opcional)
     * @param albumId ID del álbum al que pertenece la canción
     * @param token Token de autenticación del artista
     * @throws IOException Si ocurre un error al guardar el archivo de audio
     * @throws RuntimeException Si el álbum no existe o el artista no tiene acceso
     */
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

        Path songsDirectory = Paths.get("songs");
        if (!Files.exists(songsDirectory)) {
            Files.createDirectories(songsDirectory);
        }

        String fileName = System.currentTimeMillis() + "";
        Path filePath = Paths.get("songs").resolve(fileName);
        Files.copy(audioFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Song song = new Song(title, album, duration, filePath.toString());
        album.getSongs().add(song);
        songRepository.save(song);
    }

    /**
     * Obtiene una lista de canciones filtradas por artista y/o álbum.
     *
     * @param artistId ID del artista (opcional, 0 para ignorar)
     * @param albumId ID del álbum (opcional, 0 para ignorar)
     * @return Lista de canciones que cumplen los criterios
     */
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

    /**
     * Obtiene todas las canciones de los álbumes de un artista autenticado.
     *
     * @param token Token de autenticación del artista
     * @return Lista de canciones del artista
     */
    public List<Song> getArtistSongs(long token) {
        Artist artist = artistService.getActiveArtist(token);
        return artist.getAlbums()
            .stream()
            .flatMap(s -> s.getSongs().stream()).toList();
    }

    /**
     * Obtiene una canción por su ID.
     *
     * @param id ID de la canción
     * @return La canción encontrada
     * @throws RuntimeException Si la canción no existe
     */
    public Song getSong(long id) {
        Song song = songRepository.findById(id);
        if(song == null)
        {
            throw new RuntimeException("Song does not exist");
        }
        return song;
    }

    /**
     * Busca canciones por nombre.
     *
     * @param title Nombre o parte del nombre de la canción
     * @return Lista de canciones que coinciden con el nombre
     */
    public List<Song> searchSongs(String title) {
        return songRepository.findByName(title);
    }

    /**
     * Permite a un usuario dar like a una canción y la añade a su playlist de "Canciones que me gustan".
     *
     * @param songId ID de la canción a la que se da like
     * @param token Token de autenticación del usuario
     * @throws RuntimeException Si la canción no existe
     */
    public void darLike(long songId, long token) {
        // System.out.println("Song ID: " + songId);
        System.out.println("Token: " + token);
        System.out.println("Hola");
        User user = userService.getActiveUser(token);
        playlistService.addSongToPlayList(token, songId, user.getCancionesMeGustanID());
        Song song = songRepository.findById(songId);
        if (song == null) {
            throw new RuntimeException("Song does not exist");
        }
        song.setNumeroLikes(song.getNumeroLikes() + 1);
        songRepository.save(song);
    }

    /**
     * Obtiene la lista de canciones con más likes, ordenadas de mayor a menor.
     *
     * @return Lista de canciones más gustadas
     */
    public List<Song> getMostLikedSongs() {
        // Recuperar todas las canciones de la base de datos
        List<Song> songs = songRepository.findAll();

        // Filtrar las canciones con más de 0 likes
        List<Song> filteredSongs = songs.stream()
                .filter(song -> song.getNumeroLikes() > 0)
                .sorted((song1, song2) -> Integer.compare(song2.getNumeroLikes(), song1.getNumeroLikes())) // Ordenar de mayor a menor
                .toList();

        return new ArrayList<>(filteredSongs); // Convertir a ArrayList y devolver
    }

}
