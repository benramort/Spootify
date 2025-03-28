package com.deusto.theComitte.Spootify.entity;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User extends GenericUser {
    
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "FriendUsers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "user2_id")
    )
    private List<User> friendsList;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "ArtistUsers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "user2_id")
    )
    private List<Artist> followList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SongList> songsList = new ArrayList<>();

    public User(long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public User(String name, String email, String password) {
        super(name, email, password);
        this.friendsList = new ArrayList<>();
    }

    public User() {
        super();
    }

    public UserDTO toDTO() {

        List<ArtistDTO> followListDTO = new ArrayList<>();
        for(Artist artist : this.followList) {
            ArtistDTO artistDTO = artist.toDTOWithoutAlbums();
            followListDTO.add(artistDTO);
        }

        return new UserDTO(this.id, this.name, followListDTO);
    }

 
    public List<Artist> getFollowList() {
        return this.followList;
    }
    public List<SongList> getSongLists() {
        return songsList;
    }

    public void setSongLists(List<SongList> songLists) {
        this.songsList = songLists;
    }

    public void addSongList(SongList songList) {
        songsList.add(songList);
        songList.setUser(this);
    }

    public void removeSongList(SongList songList) {
        songsList.remove(songList);
        songList.setUser(null);
    }

    public void createSongList(String name) {
        SongList songList = new SongList(name, this);
        songsList.add(songList);
        songList.setUser(this);
    }
}
