package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;

public class AlbumTest {

    private Album album;
    private List<Song> songs;
    private List<Artist> artists;
    private Artist artist1, artist2;
    private Song song1, song2;
    
    @BeforeEach
    void setUp() {
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        
        // Create test artists
        artist1 = new Artist(1, "Artist 1", "artist1@test.com", "password");
        artist2 = new Artist(2, "Artist 2", "artist2@test.com", "password");
        
        // Create test songs
        song1 = new Song(1, "Song 1", null, 300, "youtubeUrl1");
        song2 = new Song(2, "Song 2", null, 300, "youtubeUrl1");
    }

    @Test
    @DisplayName("Test full constructor")
    void testFullConstructor() {
        // Add items to lists
        artists.add(artist1);
        songs.add(song1);
        
        // Create album with all parameters
        album = new Album(1, "Test Album", artists, songs);
        
        // Assertions
        assertEquals(1, album.getId());
        assertEquals("Test Album", album.getName());
        assertEquals(artists, album.getArtists());
        assertEquals(songs, album.getSongs());
        assertEquals(1, album.getArtists().size());
        assertEquals(1, album.getSongs().size());
    }
    
    @Test
    @DisplayName("Test constructor with ID and name")
    void testIdNameConstructor() {
        album = new Album(1, "Test Album");
        
        assertEquals(1, album.getId());
        assertEquals("Test Album", album.getName());
        assertNotNull(album.getArtists());
        assertNotNull(album.getSongs());
        assertTrue(album.getArtists().isEmpty());
        assertTrue(album.getSongs().isEmpty());
    }
    
    @Test
    @DisplayName("Test constructor with name only")
    void testNameOnlyConstructor() {
        album = new Album("Test Album");
        
        assertEquals("Test Album", album.getName());
        assertEquals(0, album.getId()); // Default value for long
        assertNotNull(album.getArtists());
        assertNotNull(album.getSongs());
        assertTrue(album.getArtists().isEmpty());
        assertTrue(album.getSongs().isEmpty());
    }
    
    @Test
    @DisplayName("Test empty constructor")
    void testEmptyConstructor() {
        album = new Album();
        
        assertEquals(0, album.getId()); // Default value for long
        assertNull(album.getName());
        assertNull(album.getArtists());
        assertNull(album.getSongs());
    }
    
    @Test
    @DisplayName("Test getter and setter methods")
    void testGettersAndSetters() {
        album = new Album();
        
        album.setId(10);
        assertEquals(10, album.getId());
        
        album.setName("Updated Album");
        assertEquals("Updated Album", album.getName());
        
        album.setArtists(artists);
        assertEquals(artists, album.getArtists());
        
        album.setSongs(songs);
        assertEquals(songs, album.getSongs());
    }
    
    @Test
    @DisplayName("Test add and remove methods")
    void testAddAndRemoveMethods() {
        album = new Album(1, "Test Album");
        
        // Add artists
        album.addArtist(artist1);
        assertEquals(1, album.getArtists().size());
        assertTrue(album.getArtists().contains(artist1));
        
        album.addArtist(artist2);
        assertEquals(2, album.getArtists().size());
        assertTrue(album.getArtists().contains(artist2));
        
        // Add songs
        album.addSong(song1);
        assertEquals(1, album.getSongs().size());
        assertTrue(album.getSongs().contains(song1));
        
        album.addSong(song2);
        assertEquals(2, album.getSongs().size());
        assertTrue(album.getSongs().contains(song2));
        
        // Remove song
        album.removeSong(song1);
        assertEquals(1, album.getSongs().size());
        assertFalse(album.getSongs().contains(song1));
        assertTrue(album.getSongs().contains(song2));
    }
    
    @Test
    @DisplayName("Test equals method")
    void testEquals() {
        album = new Album(1, "Test Album");
        Album sameIdAlbum = new Album(1, "Different Name");
        Album differentIdAlbum = new Album(2, "Test Album");
        
        // Same object reference
        assertTrue(album.equals(album));
        
        // Same ID, different name
        assertTrue(album.equals(sameIdAlbum));
        
        // Different ID, same name
        assertFalse(album.equals(differentIdAlbum));
        
        // Null check
        assertFalse(album.equals(null));
        
        // Different class
        assertFalse(album.equals("Not an Album"));
    }
    
    @Test
    @DisplayName("Test toDTOWithoutSongs method")
    void testToDTOWithoutSongs() {
        // Setup
        artists.add(artist1);
        album = new Album(1, "Test Album", artists, songs);
        song1.setAlbum(album);
        album.addSong(song1);
        
        // Convert to DTO
        AlbumDTO dto = album.toDTOWithoutSongs();
        
        // Assertions
        assertEquals(1, dto.getId());
        assertEquals("Test Album", dto.getName());
        assertNotNull(dto.getArtists());
        assertNull(dto.getSongs());
        assertEquals(1, dto.getArtists().size());
        
        // Check artist conversion
        ArtistDTO artistDTO = dto.getArtists().get(0);
        assertEquals(1, artistDTO.getId());
        assertEquals("Artist 1", artistDTO.getName());
    }
    
    @Test
    @DisplayName("Test toDTOWithoutArtists method")
    void testToDTOWithoutArtists() {
        // Setup
        album = new Album(1, "Test Album");
        song1.setAlbum(album);
        album.addSong(song1);
        
        // Convert to DTO
        AlbumDTO dto = album.toDTOWithoutArtists();
        
        // Assertions
        assertEquals(1, dto.getId());
        assertEquals("Test Album", dto.getName());
        assertNull(dto.getArtists());
        assertNotNull(dto.getSongs());
        assertEquals(1, dto.getSongs().size());
        
        // Check song conversion
        assertEquals(1, dto.getSongs().get(0).getId());
        assertEquals("Song 1", dto.getSongs().get(0).getTitle());
    }
    
    @Test
    @DisplayName("Test complete toDTO method")
    void testToDTO() {
        // Setup
        artists.add(artist1);
        album = new Album(1, "Test Album", artists, new ArrayList<>());
        song1.setAlbum(album);
        album.addSong(song1);
        
        // Convert to DTO
        AlbumDTO dto = album.toDTO();
        
        // Assertions
        assertEquals(1, dto.getId());
        assertEquals("Test Album", dto.getName());
        assertNotNull(dto.getArtists());
        assertNotNull(dto.getSongs());
        assertEquals(1, dto.getArtists().size());
        assertEquals(1, dto.getSongs().size());
    }
}
