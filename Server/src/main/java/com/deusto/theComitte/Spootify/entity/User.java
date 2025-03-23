package com.deusto.theComitte.Spootify.entity;

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
        return new UserDTO(this.id, this.name);
    }

    public UserDTO toDTOWithFriends() {
        List<UserDTO> friendsDTO = new ArrayList<>();
        for(User user : this.friendsList) {
            UserDTO userDTO = user.toDTO();
            friendsDTO.add(userDTO);
        }
        return new UserDTO(this.id, this.name, friendsDTO);
    }

    public List<Artist> getFollowList() {
        return this.followList;
    }
}
