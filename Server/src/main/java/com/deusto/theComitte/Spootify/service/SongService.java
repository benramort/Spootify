package com.deusto.theComitte.Spootify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
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
    ArtistService artistService;


    public void createSong(String title, int duration, String youtubeUrl, long albumId, long token) {
        Artist artist = artistService.getActiveArtists().get(token);
        List<Album> albums = artist.getAlbums();
        System.out.println("Albums: " + albums);
        Album album = albumRepository.findById(albumId);
        if(artist == null) {
            throw new RuntimeException("Artist not logged in");
        }
        if(!albums.contains(album))
        {
            throw new RuntimeException("Album does not exist");
        }
        if (!album.getArtists().contains(artist)) {
            album.getArtists().forEach(a -> System.out.println(a.getId()));
            throw new RuntimeException("Artist does not have access to this album");
        }
        Song song = new Song(title, album, duration, youtubeUrl);
        album.getSongs().add(song);
        songRepository.save(song);

    }

    public List<Song> getSongs(long artistId, long albumId) {
        if (artistId != 0) {
            //TODO
        }
        if (albumId != 0) {
            return songRepository.findByAlbumId(albumId);
        }
        return songRepository.findAll();
    }
}
