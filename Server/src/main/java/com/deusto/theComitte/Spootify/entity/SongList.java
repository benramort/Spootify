package com.deusto.theComitte.Spootify.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
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
    @Column(nullable = false)
    private boolean isPublic = false;
    @Column(nullable = true)
    private String shareLink;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "SONG_LIST_SONGS",
        joinColumns = @JoinColumn(name = "song_list_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public SongList() {}

    public SongList(String name, boolean isPublic, User user) {
        this.name = name;
        this.isPublic = isPublic;
        this.shareLink = "";
        this.user = user;
        this.songs = new ArrayList<>();
    }

    public SongList(Long id, String name, boolean isPublic, User user) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.shareLink = "";
        this.user = user;
        this.songs = new ArrayList<>();
    }

    public SongList(Long id, String name, boolean isPublic, User user, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.shareLink = "";
        this.user = user;
        this.songs = songs;
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
        List<SongDTO> songs = new ArrayList<>();
        for (Song song : this.songs) {
            songs.add(new SongDTO(song.getId(), song.getName(), song.getAlbum().toDTOWithoutSongs(), song.getDuration(), song.getSongPath()));
        }
        return new SongListDTO(this.id, this.name, this.isPublic, this.shareLink, songs);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SongList)) return false;
        SongList songList = (SongList) o;
        return id.equals(songList.id) && name.equals(songList.name) && isPublic == songList.isPublic && songs.equals(songList.songs) && user.equals(songList.user);
    }
}