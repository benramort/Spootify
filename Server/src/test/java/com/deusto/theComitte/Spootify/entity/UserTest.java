package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;

public class UserTest {
    
    private User user;
    private Artist artist1, artist2;
    private SongList songList1, songList2;
    
    @BeforeEach
    void setUp() {
        // Create test user
        user = new User(1L, "Test User", "test@example.com", "password123");
        
        // Create test artists
        artist1 = new Artist(1L, "Artist One", "artist1@example.com", "pass1");
        artist2 = new Artist(2L, "Artist Two", "artist2@example.com", "pass2");
        
        // Create test song lists
        songList1 = new SongList(1L, "Playlist One", user);
        songList2 = new SongList(2L, "Playlist Two", user);
        
        // Add artists to follow list
        List<Artist> followList = new ArrayList<>();
        followList.add(artist1);
        followList.add(artist2);
        user.getFollowList().addAll(followList);
        
        // Add song lists to user
        user.addSongList(songList1);
        user.addSongList(songList2);
    }
    
    @Test
    @DisplayName("Test constructor with id, name, email, password")
    void testFullConstructor() {
        User testUser = new User(10L, "Full Constructor", "full@example.com", "fullpass");
        
        assertEquals(10L, testUser.getId());
        assertEquals("Full Constructor", testUser.getName());
        assertEquals("full@example.com", testUser.getEmail());
        assertEquals("fullpass", testUser.getPassword());
        assertNotNull(testUser.getFollowList());
        assertTrue(testUser.getFollowList().isEmpty());
        assertNotNull(testUser.getSongLists());
        assertTrue(testUser.getSongLists().isEmpty());
    }
    
    @Test
    @DisplayName("Test constructor with name, email, password")
    void testNameEmailPasswordConstructor() {
        User testUser = new User("Name Constructor", "name@example.com", "namepass");
        
        assertEquals(0L, testUser.getId());
        assertEquals("Name Constructor", testUser.getName());
        assertEquals("name@example.com", testUser.getEmail());
        assertEquals("namepass", testUser.getPassword());
        assertNotNull(testUser.getFollowList());
        assertTrue(testUser.getFollowList().isEmpty());
        assertNotNull(testUser.getSongLists());
        assertTrue(testUser.getSongLists().isEmpty());
    }
    
    @Test
    @DisplayName("Test empty constructor")
    void testEmptyConstructor() {
        User testUser = new User();
        
        assertEquals(0L, testUser.getId());
        assertNull(testUser.getName());
        assertNull(testUser.getEmail());
        assertNull(testUser.getPassword());
        assertNull(testUser.getFollowList());
    }
    
    @Test
    @DisplayName("Test toDTO method")
    void testToDTO() {
        UserDTO dto = user.toDTO();
        
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        
        // Check follow list
        assertNotNull(dto.getUserFollows());
        assertEquals(2, dto.getUserFollows().size());
        
        // Check first artist DTO
        ArtistDTO artistDTO1 = dto.getUserFollows().get(0);
        assertEquals(1L, artistDTO1.getId());
        assertEquals("Artist One", artistDTO1.getName());
        
        // Check second artist DTO
        ArtistDTO artistDTO2 = dto.getUserFollows().get(1);
        assertEquals(2L, artistDTO2.getId());
        assertEquals("Artist Two", artistDTO2.getName());
        
        // Check song lists
        assertNotNull(dto.getPlaylists());
        assertEquals(2, dto.getPlaylists().size());
        
        // Check first song list DTO
        SongListDTO songListDTO1 = dto.getPlaylists().get(0);
        assertEquals(1L, songListDTO1.getId());
        assertEquals("Playlist One", songListDTO1.getName());
        
        // Check second song list DTO
        SongListDTO songListDTO2 = dto.getPlaylists().get(1);
        assertEquals(2L, songListDTO2.getId());
        assertEquals("Playlist Two", songListDTO2.getName());
    }
    
    @Test
    @DisplayName("Test toDTOWithoutFollowing method")
    void testToDTOWithoutFollowing() {
        UserDTO dto = user.toDTOWithoutFollowing();
        
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertNull(dto.getUserFollows());
        assertNull(dto.getPlaylists());
    }
    
    @Test
    @DisplayName("Test getFollowList method")
    void testGetFollowList() {
        List<Artist> followList = user.getFollowList();
        
        assertNotNull(followList);
        assertEquals(2, followList.size());
        assertTrue(followList.contains(artist1));
        assertTrue(followList.contains(artist2));
    }
    
    @Test
    @DisplayName("Test getSongLists method")
    void testGetSongLists() {
        List<SongList> songLists = user.getSongLists();
        
        assertNotNull(songLists);
        assertEquals(2, songLists.size());
        assertTrue(songLists.contains(songList1));
        assertTrue(songLists.contains(songList2));
    }
    
    @Test
    @DisplayName("Test setSongLists method")
    void testSetSongLists() {
        // Create a new list
        List<SongList> newSongLists = new ArrayList<>();
        SongList newSongList = new SongList(3L, "New Playlist", user);
        newSongLists.add(newSongList);
        
        // Set the new list
        user.setSongLists(newSongLists);
        
        // Verify
        List<SongList> retrievedLists = user.getSongLists();
        assertEquals(1, retrievedLists.size());
        assertTrue(retrievedLists.contains(newSongList));
        assertFalse(retrievedLists.contains(songList1));
        assertFalse(retrievedLists.contains(songList2));
    }
    
    @Test
    @DisplayName("Test addSongList method")
    void testAddSongList() {
        // Initial size
        assertEquals(2, user.getSongLists().size());
        
        // Create new song list
        SongList newSongList = new SongList(3L, "Added Playlist", null);
        
        // Add the new song list
        user.addSongList(newSongList);
        
        // Check new size
        assertEquals(3, user.getSongLists().size());
        assertTrue(user.getSongLists().contains(newSongList));
        
        // Check bidirectional relationship
        assertEquals(user, newSongList.getUser());
    }
    
    @Test
    @DisplayName("Test removeSongList method")
    void testRemoveSongList() {
        // Initial size
        assertEquals(2, user.getSongLists().size());
        
        // Remove song list
        user.removeSongList(songList1);
        
        // Check new size
        assertEquals(1, user.getSongLists().size());
        assertFalse(user.getSongLists().contains(songList1));
        assertTrue(user.getSongLists().contains(songList2));
        
        // Check bidirectional relationship
        assertNull(songList1.getUser());
    }
    
    // @Test
    // @DisplayName("Test inheritance from GenericUser")
    // void testInheritance() {
    //     // Test that User inherits equals and hashCode from GenericUser
    //     User sameIdUser = new User(1L, "Different Name", "diff@example.com", "diffpass");
    //     User differentIdUser = new User(99L, "Test User", "test@example.com", "password123");
        
    //     // Same ID should be equal
    //     assertTrue(user.equals(sameIdUser));
    //     assertTrue(sameIdUser.equals(user));
    //     assertEquals(user.hashCode(), sameIdUser.hashCode());
        
    //     // Different ID should not be equal
    //     assertFalse(user.equals(differentIdUser));
    //     assertFalse(differentIdUser.equals(user));
    //     assertNotEquals(user.hashCode(), differentIdUser.hashCode());
    // }
    
    @Test
    @DisplayName("Test handling empty lists in toDTO")
    void testToDTOWithEmptyLists() {
        // Create a user with empty lists
        User emptyUser = new User(5L, "Empty User", "empty@example.com", "emptypass");
        // Initialize empty follow list (constructor does this)
        // Initialize empty song list
        emptyUser.setSongLists(new ArrayList<>());
        
        // Get DTO
        UserDTO dto = emptyUser.toDTO();
        
        // Check results
        assertEquals(5L, dto.getId());
        assertEquals("Empty User", dto.getName());
        assertNotNull(dto.getUserFollows());
        assertTrue(dto.getUserFollows().isEmpty());
        assertNotNull(dto.getPlaylists());
        assertTrue(dto.getPlaylists().isEmpty());
    }
    
    @Test
    @DisplayName("Test handling null lists in toDTO")
    void testToDTOWithNullLists() {
        // Create a user with null lists
        User nullUser = new User(6L, "Null User", "null@example.com", "nullpass");
        
        // Explicitly set follow list to null (by reflection since it's private)
        try {
            java.lang.reflect.Field followListField = User.class.getDeclaredField("followList");
            followListField.setAccessible(true);
            followListField.set(nullUser, null);
        } catch (Exception e) {
            fail("Failed to set followList to null: " + e.getMessage());
        }
        
        // Try to get DTO - this might throw NullPointerException
        assertThrows(NullPointerException.class, () -> nullUser.toDTO());
    }
}