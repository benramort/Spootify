package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class PlaylistService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    PlayListRepository songListRepository;

    @Autowired
    private UserService userService;

    public void addSongsToUser(long userId, List<Long> songIds, long songListId) {
        User user = userService.getActiveUser(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        for (long songId : songIds) {
            Song song = songRepository.findById(songId);
            if (song == null) {
                throw new RuntimeException("Song does not exist");
            }

            for (SongList songList : user.getSongLists()) {
                if (songList.getId().equals(songListId)) {
                    songList.getSongs().add(song);
                    songList.setUser(user);
                    return;
                }
            }
        }
    }

    public void addSongToPlayList(long userId, long songId, long songListId) {
        User user = userService.getActiveUser(userId); // Obtén el usuario activo
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
    
        Song song = songRepository.findById(songId); // Busca la canción en la base de datos
        if (song == null) {
            throw new RuntimeException("Song does not exist");
        }
    
        SongList songList = songListRepository.findById(songListId); // Busca la playlist en la base de datos
        if (songList == null) {
            throw new RuntimeException("SongList does not exist");
        }
    
        // Añade la canción a la playlist
        songList.getSongs().add(song);
    
        // Guarda la playlist en la base de datos
        songListRepository.save(songList);
        userRepository.save(user); // Guarda el usuario en la base de datos
    }

    public void createPlayList(long token, String name) {
        User user = userService.getActiveUser(token);
        if (user == null) {
            System.out.println("aaaaaaaaaaaaa");
            throw new RuntimeException("User not logged in");
        }
        SongList songList = new SongList(name, user);
        user.addSongList(songList);
        userRepository.save(user);
        songListRepository.save(songList);
    }

    public List<SongList> getPlayLists(long userId) {
        User user = userService.getActiveUser(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        User updatedUser = userRepository.findById(user.getId()); //Beñat del futuro, busca una manera de hacer esto mejor
        return updatedUser.getSongLists();
    }

    public SongList getPlaylistById(long userId, long songListId) {
        User user = userService.getActiveUser(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        SongList songList = songListRepository.findById(songListId);
        if (songList == null) {
            throw new RuntimeException("SongList does not exist");
        }
        if (songList.getUser().getId() != user.getId()) {
            throw new RuntimeException("User does not have access to this playlist");
        }
        return songList;
    }
}
