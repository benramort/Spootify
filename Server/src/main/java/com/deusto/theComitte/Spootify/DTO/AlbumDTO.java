package com.deusto.theComitte.Spootify.DTO;

import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;



public class AlbumDTO {

    private long id;
    private String name;
    private List<ArtistDTO> artists;
    private List<SongDTO> songs;

    public AlbumDTO(long id, String name) {
        this.id = id;
        this.name = name;
        this.artists = new ArrayList<>();
        this.songs = new ArrayList<>();
    }


    public AlbumDTO(long id, String name, List<ArtistDTO> artists, List<SongDTO> songs) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.songs = songs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setArtists(List<ArtistDTO> artists) {
        this.artists = artists;
    }

    public List<ArtistDTO> getArtists() {
        return this.artists;
    }

    public void addArtist(ArtistDTO artist) {
        this.artists.add(artist);
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

    public List<SongDTO> getSongs() {
        return this.songs;
    }

    public void addSong(SongDTO song) {
        this.songs.add(song);
    }

    public void removeSong(SongDTO song) {
        this.songs.remove(song);
    }
}
