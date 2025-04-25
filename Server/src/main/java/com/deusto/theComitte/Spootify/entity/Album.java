package com.deusto.theComitte.Spootify.entity;

import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;

import jakarta.persistence.*;

@Entity
@Table(name = "Albums")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String cover;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    (
        name = "AlbumArtists",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")   
    )
    private List<Artist> artists;
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Song> songs;

    public Album() {}

    public Album(long id, String name) {
        this.id = id;
        this.name = name;
        this.artists = new ArrayList<>();
        this.songs = new ArrayList<>();
    }

    public Album(String name) {
        this.name = name;
        this.artists = new ArrayList<>();
        this.songs = new ArrayList<>();
    }

    public Album(long id, String name, List<Artist> artists, List<Song> songs) {
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

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return this.cover;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Artist> getArtists() {
        return this.artists;
    }

    public void addArtist(Artist artist) {
        this.artists.add(artist);
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
    }

    public AlbumDTO toDTOWithoutSongs() {
        ArrayList<ArtistDTO> artistsDTO = new ArrayList<>();
        for(Artist artist : this.artists)
        {
            artistsDTO.add(artist.toDTOWithoutAlbums());
        }
        return new AlbumDTO(this.id, this.name, this.cover, artistsDTO, null);
    }

    public AlbumDTO toDTOWithoutArtists() {
        ArrayList<SongDTO> songsDTO = new ArrayList<>();
        for(Song song : this.songs)
        {
            songsDTO.add(song.toDTOWithoutAlbum());
        }
        return new AlbumDTO(this.id, this.name, this.cover, null, songsDTO);
    }

    public AlbumDTO toDTO() {
        ArrayList<ArtistDTO> artistsDTO = new ArrayList<>();
        ArrayList<SongDTO> songsDTO = new ArrayList<>();
        for(Artist artist : this.artists)
        {
            artistsDTO.add(artist.toDTOWithoutAlbums());
        }
        for(Song song : this.songs)
        {
            songsDTO.add(song.toDTOWithoutAlbum());
        }
        return new AlbumDTO(this.id, this.name, this.cover, artistsDTO, songsDTO);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Album album = (Album) obj;
        return this.id == album.id;
    }
}
