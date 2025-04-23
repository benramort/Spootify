package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;

public class SongTest {

    private Song song;
    private Album album;
    private final long SONG_ID = 1L;
    private final String SONG_NAME = "Test Song";
    private final int SONG_DURATION = 180; // 3 minutes in seconds
    private final String YOUTUBE_URL = "https://youtube.com/watch?v=test123";
    
    @BeforeEach
    void setUp() {
        // Create test album with artists
        album = new Album(1L, "Test Album");
        Artist artist = new Artist(1L, "Test Artist", "artist@example.com", "password");
        album.addArtist(artist);
        
        // Create test song with the album
        song = new Song(SONG_ID, SONG_NAME, album, SONG_DURATION, YOUTUBE_URL);
    }
    
    @Test
    @DisplayName("Test full constructor")
    void testFullConstructor() {
        assertEquals(SONG_ID, song.getId());
        assertEquals(SONG_NAME, song.getName());
        assertEquals(album, song.getAlbum());
        assertEquals(SONG_DURATION, song.getDuration());
        assertEquals(YOUTUBE_URL, song.getYoutubeUrl());
    }
    
    @Test
    @DisplayName("Test constructor without ID")
    void testConstructorWithoutId() {
        Song songWithoutId = new Song(SONG_NAME, album, SONG_DURATION, YOUTUBE_URL);
        
        assertEquals(0L, songWithoutId.getId()); // Default value for uninitialized long
        assertEquals(SONG_NAME, songWithoutId.getName());
        assertEquals(album, songWithoutId.getAlbum());
        assertEquals(SONG_DURATION, songWithoutId.getDuration());
        assertEquals(YOUTUBE_URL, songWithoutId.getYoutubeUrl());
    }
    
    @Test
    @DisplayName("Test empty constructor")
    void testEmptyConstructor() {
        Song emptySong = new Song();
        
        assertEquals(0L, emptySong.getId());
        assertNull(emptySong.getName());
        assertNull(emptySong.getAlbum());
        assertEquals(0, emptySong.getDuration());
        assertNull(emptySong.getYoutubeUrl());
    }
    
    @Test
    @DisplayName("Test setter and getter for ID")
    void testIdSetterGetter() {
        song.setId(5L);
        assertEquals(5L, song.getId());
    }
    
    @Test
    @DisplayName("Test setter and getter for name")
    void testNameSetterGetter() {
        song.setName("New Song Name");
        assertEquals("New Song Name", song.getName());
    }
    
    @Test
    @DisplayName("Test setter and getter for album")
    void testAlbumSetterGetter() {
        Album newAlbum = new Album(2L, "New Album");
        song.setAlbum(newAlbum);
        assertEquals(newAlbum, song.getAlbum());
    }
    
    @Test
    @DisplayName("Test setter and getter for duration")
    void testDurationSetterGetter() {
        song.setDuration(240);
        assertEquals(240, song.getDuration());
    }
    
    @Test
    @DisplayName("Test setter and getter for YouTube URL")
    void testYoutubeUrlSetterGetter() {
        song.setYoutubeUrl("https://youtube.com/watch?v=newurl456");
        assertEquals("https://youtube.com/watch?v=newurl456", song.getYoutubeUrl());
    }
    
    @Test
    @DisplayName("Test toDTO method with properly initialized album")
    void testToDTO() {
        // Create a proper album with artists
        Album testAlbum = new Album(3L, "DTO Test Album");
        Artist testArtist = new Artist(2L, "DTO Test Artist", "dtoartist@example.com", "pass");
        testAlbum.addArtist(testArtist);
        
        // Set proper album to song
        song.setAlbum(testAlbum);
        
        // Call the method under test
        SongDTO dto = song.toDTO();
        
        // Verify the result
        assertNotNull(dto);
        assertEquals(SONG_ID, dto.getId());
        assertEquals(SONG_NAME, dto.getTitle());
        assertEquals(SONG_DURATION, dto.getDuration());
        assertEquals(YOUTUBE_URL, dto.getYoutubeUrl());
        
        // Verify album DTO
        assertNotNull(dto.getAlbum());
        assertEquals(testAlbum.getId(), dto.getAlbum().getId());
        assertEquals(testAlbum.getName(), dto.getAlbum().getName());
    }
    
    @Test
    @DisplayName("Test toDTOWithoutAlbum method")
    void testToDTOWithoutAlbum() {
        SongDTO dto = song.toDTOWithoutAlbum();
        
        assertNotNull(dto);
        assertEquals(SONG_ID, dto.getId());
        assertEquals(SONG_NAME, dto.getTitle());
        assertEquals(SONG_DURATION, dto.getDuration());
        assertEquals(YOUTUBE_URL, dto.getYoutubeUrl());
        assertNull(dto.getAlbum());
    }
    
    @Test
    @DisplayName("Test toDTO with null album")
    void testToDTOWithNullAlbum() {
        // Set album to null
        song.setAlbum(null);
        
        // This should throw NullPointerException
        assertThrows(NullPointerException.class, () -> song.toDTO());
    }
    
    @Test
    @DisplayName("Test handling of zero duration")
    void testZeroDuration() {
        song.setDuration(0);
        assertEquals(0, song.getDuration());
        
        SongDTO dto = song.toDTOWithoutAlbum();
        assertEquals(0, dto.getDuration());
    }
    
    // @Test
    // @DisplayName("Test handling of negative duration")
    // void testNegativeDuration() {
    //     song.setDuration(-60);
    //     assertEquals(-60, song.getDuration());
        
    //     SongDTO dto = song.toDTOWithoutAlbum();
    //     assertEquals(-60, dto.getDuration());
    // }
    
    // @Test
    // @DisplayName("Test equals method with same object")
    // void testEqualsSameObject() {
    //     assertTrue(song.equals(song));
    // }
    
    // @Test
    // @DisplayName("Test equals method with same ID")
    // void testEqualsSameId() {
    //     Song sameIdSong = new Song();
    //     song.setId(SONG_ID);
    //     song.setName("Different Name");
    //     assertTrue(song.equals(sameIdSong));
    //     assertTrue(sameIdSong.equals(song));
    // }
    
    // @Test
    // @DisplayName("Test equals method with different ID")
    // void testEqualsDifferentId() {
    //     Song differentIdSong = new Song(999L, SONG_NAME);
    //     assertFalse(song.equals(differentIdSong));
    //     assertFalse(differentIdSong.equals(song));
    // }
    
    // @Test
    // @DisplayName("Test equals method with null")
    // void testEqualsNull() {
    //     assertFalse(song.equals(null));
    // }
    
    // @Test
    // @DisplayName("Test equals method with different class")
    // void testEqualsDifferentClass() {
    //     assertFalse(song.equals("Not a Song"));
    // }
    
    // @Test
    // @DisplayName("Test hashCode method consistency with equals")
    // void testHashCode() {
    //     // Same objects should have same hash code
    //     assertEquals(song.hashCode(), song.hashCode());
        
    //     // Objects equal by ID should have same hash code
    //     Song sameIdSong = new Song(SONG_ID, "Different Name");
    //     assertEquals(song.hashCode(), sameIdSong.hashCode());
        
    //     // Different IDs should have different hash codes
    //     Song differentIdSong = new Song(999L, SONG_NAME);
    //     assertNotEquals(song.hashCode(), differentIdSong.hashCode());
    // }
}