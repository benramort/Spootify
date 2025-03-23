package com.deusto.theComitte.Spootify.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.DTO.SongListDTO;

@Entity
@Table(name = "SONG_LISTS")
public class SongList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;
    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "SONG_LIST_SONGS",
        joinColumns = @JoinColumn(name = "song_list_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public SongList() {}

    public SongList(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SongListDTO toDTO() {
        SongListDTO songListDTO = new SongListDTO();
        songListDTO.setId(this.id);
        songListDTO.setName(this.name);
        songListDTO.setUser(this.user.toDTO());
        return songListDTO;
    }
}
