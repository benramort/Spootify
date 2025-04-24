package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
import com.deusto.theComitte.Spootify.DTO.AlbumDTO;

class ArtistTest {
    
    private Artist artist;
    private User user1, user2;
    private Album album1, album2;
    private List<Album> albums;
    
    @BeforeEach
    void setUp() {
        // Initialize test data
        artist = new Artist(1L, "Test Artist", "artist@test.com", "password123");
        
        // Set up users
        user1 = new User(1L, "User One", "user1@test.com", "pass123");
        user2 = new User(2L, "User Two", "user2@test.com", "pass456");
        
        // Set up albums
        album1 = new Album(1L, "Album One");
        album2 = new Album(2L, "Album Two");
        albums = new ArrayList<>();
        albums.add(album1);
        albums.add(album2);
        
        // Make albums available through reflection (due to the field being private)
        try {
            java.lang.reflect.Field albumsField = Artist.class.getDeclaredField("albums");
            albumsField.setAccessible(true);
            albumsField.set(artist, albums);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Test constructor with id, name, email, password")
    void testFullConstructor() {
        assertEquals(1L, artist.getId());
        assertEquals("Test Artist", artist.getName());
        assertEquals("artist@test.com", artist.getEmail());
        assertEquals("password123", artist.getPassword());
        assertNotNull(artist.getFollowersList());
        assertTrue(artist.getFollowersList().isEmpty());
        assertEquals(0, artist.getFollowersList().size());
    }
    
    @Test
    @DisplayName("Test constructor with name, email, password")
    void testNameEmailPasswordConstructor() {
        Artist newArtist = new Artist("New Artist", "new@test.com", "newpass");
        
        assertEquals(0, newArtist.getId());
        assertEquals("New Artist", newArtist.getName());
        assertEquals("new@test.com", newArtist.getEmail());
        assertEquals("newpass", newArtist.getPassword());
        assertNotNull(newArtist.getFollowersList());
        assertTrue(newArtist.getFollowersList().isEmpty());
        assertEquals(0, newArtist.getFollowersList().size());
    }
    
    @Test
    @DisplayName("Test empty constructor")
    void testEmptyConstructor() {
        Artist emptyArtist = new Artist();
        
        assertEquals(0, emptyArtist.getId());
        assertNull(emptyArtist.getName());
        assertNull(emptyArtist.getEmail());
        assertNull(emptyArtist.getPassword());
        assertNull(emptyArtist.getFollowersList());
    }
    
    @Test
    @DisplayName("Test getAlbums method")
    void testGetAlbums() {
        List<Album> retrievedAlbums = artist.getAlbums();
        
        assertNotNull(retrievedAlbums);
        assertEquals(2, retrievedAlbums.size());
        assertTrue(retrievedAlbums.contains(album1));
        assertTrue(retrievedAlbums.contains(album2));
    }
    
    @Test
    @DisplayName("Test toDTOWithoutAlbums method")
    void testToDTOWithoutAlbums() {
        ArtistDTO dto = artist.toDTOWithoutAlbums();
        
        assertEquals(1L, dto.getId());
        assertEquals("Test Artist", dto.getName());
        // assertEquals(0, dto.getFollowersList().size());
        assertNull(dto.getAlbums());
        assertNull(dto.getFollowersList());
    }
    
    @Test
    @DisplayName("Test toDTO method")
    void testToDTO() {
        // Setup album with artists to avoid NPE during conversion
        album1.setArtists(new ArrayList<>());
        album1.getArtists().add(artist);
        album2.setArtists(new ArrayList<>());
        album2.getArtists().add(artist);
        
        ArtistDTO dto = artist.toDTO();
        
        assertEquals(1L, dto.getId());
        assertEquals("Test Artist", dto.getName());
        // assertEquals(0, dto.getFollowersList().size());
        assertNotNull(dto.getAlbums());
        assertEquals(2, dto.getAlbums().size());
        
        // Check album DTOs
        List<AlbumDTO> albumDTOs = dto.getAlbums();
        assertEquals(1L, albumDTOs.get(0).getId());
        assertEquals("Album One", albumDTOs.get(0).getName());
        assertEquals(2L, albumDTOs.get(1).getId());
        assertEquals("Album Two", albumDTOs.get(1).getName());
    }
    
    @Test
    @DisplayName("Test toDTOWithFollowers method")
    void testToDTOWithFollowers() {
        // Setup album with artists to avoid NPE during conversion
        album1.setArtists(new ArrayList<>());
        album1.getArtists().add(artist);
        album2.setArtists(new ArrayList<>());
        album2.getArtists().add(artist);
        
        // Add followers
        artist.followArtist(user1);
        artist.followArtist(user2);
        
        ArtistDTO dto = artist.toDTOWithFollowers();
        
        assertEquals(1L, dto.getId());
        assertEquals("Test Artist", dto.getName());
        assertEquals(2, dto.getFollowersList().size());
        assertNotNull(dto.getAlbums());
        assertEquals(2, dto.getAlbums().size());
        assertNotNull(dto.getFollowersList());
        assertEquals(2, dto.getFollowersList().size());
        
        // Check follower DTOs
        List<UserDTO> followerDTOs = dto.getFollowersList();
        assertEquals(1L, followerDTOs.get(0).getId());
        assertEquals("User One", followerDTOs.get(0).getName());
        assertEquals(2L, followerDTOs.get(1).getId());
        assertEquals("User Two", followerDTOs.get(1).getName());
    }
    
    @Test
    @DisplayName("Test equals method")
    void testEquals() {
        // Same object
        assertTrue(artist.equals(artist));
        
        // Same ID
        Artist sameIdArtist = new Artist(1L, "Different Name", "diff@test.com", "diffpass");
        assertTrue(artist.equals(sameIdArtist));
        
        // Different ID
        Artist differentIdArtist = new Artist(2L, "Test Artist", "artist@test.com", "password123");
        assertFalse(artist.equals(differentIdArtist));
        
        // Null
        assertFalse(artist.equals(null));
        
        // Different class
        assertFalse(artist.equals("Not an artist"));
    }
    
    @Test
    @DisplayName("Test getFollowersList method")
    void testGetFollowersList() {
        List<User> followersList = artist.getFollowersList();
        
        assertNotNull(followersList);
        assertTrue(followersList.isEmpty());
    }
    
    @Test
    @DisplayName("Test followArtist method")
    void testFollowArtist() {
        // Initial state
        assertEquals(0, artist.getFollowersList().size());
        assertTrue(artist.getFollowersList().isEmpty());
        assertTrue(user1.getFollowList().isEmpty());
        
        // Follow artist
        artist.followArtist(user1);
        
        // After first follow
        assertEquals(1, artist.getFollowersList().size());
        assertTrue(artist.getFollowersList().contains(user1));
        assertEquals(1, user1.getFollowList().size());
        assertTrue(user1.getFollowList().contains(artist));
        
        // Add another follower
        artist.followArtist(user2);
        
        // After second follow
        assertEquals(2, artist.getFollowersList().size());
        assertTrue(artist.getFollowersList().contains(user2));
        
        // Try to follow again (should not increase count)
        artist.followArtist(user1);
        assertEquals(2, artist.getFollowersList().size());
    }
}