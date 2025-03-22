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

    public void createAlbum(String name, long token) {
        Artist artist = artistService.getActiveArtists().get(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        System.out.println("Creating album");
        Album album = new Album(name);
        artist.getAlbums().add(album);
        album.getArtists().add(artist);
        System.out.println("Saving album");
        System.out.println(album.getArtists());
        albumRepository.save(album);
        artistRepository.save(artist);
        // System.out.println(albumRepository.findById(al));
        System.out.println("Created album");
    }

    public List<Album> getAlbums() {
        return albumRepository.findAll();
    }
}
