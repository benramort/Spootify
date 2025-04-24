package com.deusto.theComitte.Spootify.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;

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

        String fileName = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
        Path filePath = Paths.get("imagenes").resolve(fileName);
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
}
