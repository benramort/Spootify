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
        Artist artist = artistService.getActiveArtist(token);
        Album album = new Album(name);
        artist.getAlbums().add(album);
        album.getArtists().add(artist);
        System.out.println(album.getArtists());
        albumRepository.save(album);
        artistRepository.save(artist);
        // System.out.println(albumRepository.findById(al));
    }

    public List<Album> getAlbums() {
        return albumRepository.findAll();
    }
}
