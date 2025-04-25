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
    
    @Autowired
    ArtistService artistService;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    AlbumRepository albumRepository;

    public void createAlbum(String name, String cover, long token) {
        Artist artist = artistService.getActiveArtist(token);
        Album album = new Album(name);
        album.setCover(cover);
        artist.getAlbums().add(album);
        album.getArtists().add(artist);
        albumRepository.save(album);
        artistRepository.save(artist);
        // System.out.println(albumRepository.findById(al));
    }

    public List<Album> getAlbums(long artistId) {
        if(artistId != 0) {
            Artist artist = artistRepository.findById(artistId);
            return artist.getAlbums();
        }
        return albumRepository.findAll();
    }

    public Album getAlbum(long id) {
        Album album = albumRepository.findById(id);
        if (album == null) {
            throw new RuntimeException("Album not found");
        }
        return album;
    }

    public List<Album> searchAlbums(String name) {
        List<Album> albums = albumRepository.findByName(name);
        if (albums.isEmpty()) {
            throw new RuntimeException("No albums found with the given name");
        }
        return albums;
    }
    public List<Album> getArtistAlbums(long artistId) {
        Artist artist = artistService.getActiveArtist(artistId);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        return null;
    }

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }
}
