package com.deusto.theComitte.Spootify.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase SongList")
class SongListTest {

    private SongList songList;
    private User user;
    private Song song1;
    private Song song2;

    @BeforeEach
    @DisplayName("Configuración inicial para los tests")
    void setUp() {
        user = new User(1,"nombre", "email", "password");

        songList = new SongList();
        song1 = new Song();
        song1.setId(1L);
        song1.setName("Song 1");

        song2 = new Song();
        song2.setId(2L);
        song2.setName("Song 2");
    }

    @Test
    @DisplayName("Test para setId y getId")
    void testSetAndGetId() {
        songList.setId(10L);
        assertEquals(10L, songList.getId());
    }

    @Test
    @DisplayName("Test para setName y getName")
    void testSetAndGetName() {
        songList.setName("My Playlist");
        assertEquals("My Playlist", songList.getName());
    }

    @Test
    @DisplayName("Test para setSongs y getSongs")
    void testSetAndGetSongs() {
        List<Song> songs = new ArrayList<>();
        songs.add(song1);
        songs.add(song2);

        songList.setSongs(songs);
        assertEquals(2, songList.getSongs().size());
        assertTrue(songList.getSongs().contains(song1));
        assertTrue(songList.getSongs().contains(song2));
    }

    @Test
    @DisplayName("Test para setUser y getUser")
    void testSetAndGetUser() {
        songList.setUser(user);
        assertEquals(user, songList.getUser());
    }
}
