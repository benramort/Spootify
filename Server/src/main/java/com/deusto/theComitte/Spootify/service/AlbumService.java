package com.deusto.theComitte.Spootify.service;

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

    public void createAlbum(long id, String name, long token) {
        Artist artist = artistService.getActiveArtists().get(token);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        Album album = new Album(id, name);
        artist.getAlbums().add(album);
        artistRepository.save(artist);
        albumRepository.save(album);
    }
}
