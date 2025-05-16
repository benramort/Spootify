package com.deusto.theComitte.Spootify.entity;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "Songs")
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id")
    private Album album;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false, unique = true)
    private String songPath;
    private int numeroLikes;

    public Song(long id, String name, Album album, int duration, String songPath) {
        this.id = id;
        this.album = album;
        this.name = name;
        this.duration = duration;
        this.songPath = songPath;
        this.numeroLikes = 0;
    }
    
    public Song(String name, Album album, int duration, String songPath) {
        this.album = album;
        this.name = name;
        this.duration = duration;
        this.songPath = songPath;
        this.numeroLikes = 0;
    }

    public Song() {

    }

    public long getId() {
        return id;
    }

    public Album getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumeroLikes() {
        return numeroLikes;
    }

    public void setNumeroLikes(int numeroLikes) {
        this.numeroLikes = numeroLikes;
    }

    public SongDTO toDTO() {
        return new SongDTO(this.id, this.name, this.album.toDTOWithoutSongs(), this.duration, this.songPath, this.numeroLikes);
    }

    public SongDTO toDTOWithoutAlbum() {
        return new SongDTO(this.id, this.name, null, this.duration, this.songPath, this.numeroLikes);
    }
}
