package com.deusto.theComitte.Spootify.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.deusto.theComitte.Spootify.DTO.SongListDTO;

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
        user = new User(1, "Test User", "test@example.com", "password");

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

    @Test
    @DisplayName("Test para añadir canciones a la lista")
    void testAddSongs() {
        songList.getSongs().add(song1);
        songList.getSongs().add(song2);

        assertEquals(2, songList.getSongs().size());
        assertTrue(songList.getSongs().contains(song1));
        assertTrue(songList.getSongs().contains(song2));
    }

    @Test
    @DisplayName("Test para convertir SongList a DTO")
    void testToDTO() {
        // Crear álbum para las canciones
        Album album = new Album(1L, "Test Album");

        // Configurar canciones con álbum
        song1.setAlbum(album);
        song2.setAlbum(album);

        // Crear lista de canciones
        List<Song> songs = new ArrayList<>();
        songs.add(song1);
        songs.add(song2);

        // Configurar SongList
        songList.setId(1L);
        songList.setName("My Playlist");
        songList.setSongs(songs);
        songList.setUser(user);

        // Convertir a DTO
        SongListDTO dto = songList.toDTO();

        // Verificar los valores del DTO
        assertEquals(1L, dto.getId());
        assertEquals("My Playlist", dto.getName());
        assertNotNull(dto.getSongs());
        assertEquals(2, dto.getSongs().size());

        // Verificar que las canciones se conviertan correctamente
        assertEquals(song1.getId(), dto.getSongs().get(0).getId());
        assertEquals(song1.getName(), dto.getSongs().get(0).getTitle());
        assertEquals(album.getId(), dto.getSongs().get(0).getAlbum().getId());
        assertEquals(album.getName(), dto.getSongs().get(0).getAlbum().getName());

        assertEquals(song2.getId(), dto.getSongs().get(1).getId());
        assertEquals(song2.getName(), dto.getSongs().get(1).getTitle());
        assertEquals(album.getId(), dto.getSongs().get(1).getAlbum().getId());
        assertEquals(album.getName(), dto.getSongs().get(1).getAlbum().getName());
    }

    @Test
    @DisplayName("Test equals method")
    void testEquals() {
        SongList songList = new SongList(1L, "Test Playlist", true, user, new ArrayList<>());
        SongList songList2 = new SongList(1L, "Test Playlist", true, user, new ArrayList<>());
        SongList sameIdSongList = new SongList(1L, "Different Name", true, user, new ArrayList<>());
        SongList differentIdSongList = new SongList(2L, "Test Playlist", true, user, new ArrayList<>());

        // Same ID, name, songs and user
        assertTrue(songList.equals(songList2));

        // Same object reference
        assertTrue(songList.equals(songList));

        // Same ID, different name
        assertFalse(songList.equals(sameIdSongList));

        // Different ID, same name
        assertFalse(songList.equals(differentIdSongList));

        // Null check
        assertFalse(songList.equals(null));

        // Different class
        assertFalse(songList.equals("Not a SongList"));
    }

    @Test
    @DisplayName("Test equals method with null attributes")
    void testEqualsWithNullAttributes() {
        SongList songList = new SongList(1L, "Test Playlist", true, user, new ArrayList<>());

        // Caso donde el ID es null
        SongList songListWithNullId = new SongList(null, "Test Playlist", true, user, new ArrayList<>());
        assertFalse(songList.equals(songListWithNullId));

        // Caso donde el nombre es null
        SongList songListWithNullName = new SongList(1L, null, true, user, new ArrayList<>());
        assertFalse(songList.equals(songListWithNullName));

        // Caso donde la lista de canciones es null
        SongList songListWithNullSongs = new SongList(1L, "Test Playlist", true, user, null);
        assertFalse(songList.equals(songListWithNullSongs));

        // Caso donde el usuario es null
        SongList songListWithNullUser = new SongList(1L, "Test Playlist", true, null, new ArrayList<>());
        assertFalse(songList.equals(songListWithNullUser));


    }

    @Test
    @DisplayName("Test para el constructor vacío")
    void testEmptyConstructor() {
        SongList emptySongList = new SongList();
        assertNotNull(emptySongList);
        assertNull(emptySongList.getId());
        assertNull(emptySongList.getName());
        assertNull(emptySongList.getUser());
        assertNotNull(emptySongList.getSongs());
        assertTrue(emptySongList.getSongs().isEmpty());
    }

    @Test
    @DisplayName("Test para el constructor con nombre y usuario")
    void testConstructorWithNameAndUser() {
        SongList newSongList = new SongList("My Playlist", true, user);
        assertEquals("My Playlist", newSongList.getName());
        assertEquals(user, newSongList.getUser());
        assertNotNull(newSongList.getSongs());
        assertTrue(newSongList.getSongs().isEmpty());
    }

    @Test
    @DisplayName("Test para el constructor completo")
    void testFullConstructor() {
        List<Song> songs = new ArrayList<>();
        songs.add(song1);
        songs.add(song2);

        SongList fullSongList = new SongList(1L, "My Playlist", true, user, songs);
        assertEquals(1L, fullSongList.getId());
        assertEquals("My Playlist", fullSongList.getName());
        assertEquals(user, fullSongList.getUser());
        assertEquals(2, fullSongList.getSongs().size());
    }
}