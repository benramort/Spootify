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
    private String youtubeUrl;

    public Song(String name, Album album, int duration, String youtubeUrl) {
        this.album = album;
        this.name = name;
        this.duration = duration;
        this.youtubeUrl = youtubeUrl;
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

    public String getYoutubeUrl() {
        return youtubeUrl;
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

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SongDTO toDTO() {
        return new SongDTO(this.id, this.name, this.album.toDto(), this.duration, this.youtubeUrl);
    }
}
