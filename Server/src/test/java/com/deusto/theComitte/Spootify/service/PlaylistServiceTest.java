package com.deusto.theComitte.Spootify.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

public class PlaylistServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ArtistRepository artistRepository;
    
    @Mock
    private SongRepository songRepository;
    
    @Mock
    private PlayListRepository songListRepository;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private PlaylistService playlistService;
    
    private User testUser;
    private Song testSong;
    private SongList testPlaylist;
    private final long TOKEN = 12345L;
    private final long USER_ID = 1L;
    private final long SONG_ID = 1L;
    private final long PLAYLIST_ID = 1L;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User(USER_ID, "testuser", "test@example.com", "password");
        
        // Create test song
        Album testAlbum = new Album(1L, "Test Album");
        testSong = new Song(SONG_ID, "Test Song", testAlbum, 180, "https://youtube.com/test");
        
        // Create test playlist
        testPlaylist = new SongList(PLAYLIST_ID, "Test Playlist", false, testUser);
        
        
        // Set up user with playlist
        List<SongList> userPlaylists = new ArrayList<>();
        userPlaylists.add(testPlaylist);
        testUser.setSongLists(userPlaylists);
    }
    
    @Test
    @DisplayName("Add song to playlist successfully")
    void testAddSongToPlaylistSuccess() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        when(songRepository.findById(SONG_ID)).thenReturn(testSong);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(testPlaylist);
        
        // Act
        playlistService.addSongToPlayList(USER_ID, SONG_ID, PLAYLIST_ID);
        
        // Assert
        verify(songListRepository).save(testPlaylist);
        verify(userRepository).save(testUser);
        assertTrue(testPlaylist.getSongs().contains(testSong));
    }
    
    @Test
    @DisplayName("Add song to playlist fails when user does not exist")
    void testAddSongToPlaylistFailsWhenUserDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.addSongToPlayList(USER_ID, SONG_ID, PLAYLIST_ID)
        );
        assertEquals("User does not exist", exception.getMessage());
        verify(songListRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Add song to playlist fails when song does not exist")
    void testAddSongToPlaylistFailsWhenSongDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        when(songRepository.findById(SONG_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.addSongToPlayList(USER_ID, SONG_ID, PLAYLIST_ID)
        );
        assertEquals("Song does not exist", exception.getMessage());
        verify(songListRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Add song to playlist fails when playlist does not exist")
    void testAddSongToPlaylistFailsWhenPlaylistDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        when(songRepository.findById(SONG_ID)).thenReturn(testSong);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.addSongToPlayList(USER_ID, SONG_ID, PLAYLIST_ID)
        );
        assertEquals("SongList does not exist", exception.getMessage());
        verify(songListRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Create playlist successfully")
    void testCreatePlayListSuccess() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        
        // Act
        playlistService.createPlayList(USER_ID, "New Playlist", true);
        
        // Assert
        verify(userRepository).save(testUser);
        verify(songListRepository).save(any(SongList.class));
        assertEquals(2, testUser.getSongLists().size());
        assertEquals("New Playlist", testUser.getSongLists().get(1).getName());
        assertEquals(true, testUser.getSongLists().get(1).getIsPublic());
    }
    
    @Test
    @DisplayName("Create playlist fails when user does not exist")
    void testCreatePlayListFailsWhenUserDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.createPlayList(USER_ID, "New Playlist", true)
        );
        assertEquals("User not logged in", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(songListRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Get playlists for user successfully")
    void testGetPlayListsSuccess() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);
        when(userRepository.findById(USER_ID)).thenReturn(testUser);
        
        // Act
        List<SongList> result = playlistService.getPlayLists(TOKEN);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Playlist", result.get(0).getName());
        verify(userRepository).findById(USER_ID);
    }
    
    @Test
    @DisplayName("Get playlists fails when user does not exist")
    void testGetPlayListsFailsWhenUserDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.getPlayLists(USER_ID)
        );
        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, never()).findById(any());
    }
    
    @Test
    @DisplayName("Get playlist by ID successfully")
    void testGetPlaylistByIdSuccess() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(testPlaylist);
        
        // Act
        SongList result = playlistService.getPlaylistById(USER_ID, PLAYLIST_ID);
        
        // Assert
        assertEquals(testPlaylist, result);
        assertEquals("Test Playlist", result.getName());
    }
    
    @Test
    @DisplayName("Get playlist by ID fails when user does not exist")
    void testGetPlaylistByIdFailsWhenUserDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.getPlaylistById(USER_ID, PLAYLIST_ID)
        );
        assertEquals("User does not exist", exception.getMessage());
    }
    
    @Test
    @DisplayName("Get playlist by ID fails when playlist does not exist")
    void testGetPlaylistByIdFailsWhenPlaylistDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(USER_ID)).thenReturn(testUser);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.getPlaylistById(USER_ID, PLAYLIST_ID)
        );
        assertEquals("SongList does not exist", exception.getMessage());
    }
    
    @Test
    @DisplayName("Get playlist by ID fails when user does not have access")
    void testGetPlaylistByIdFailsWhenUserDoesNotHaveAccess() {
        // Arrange
        User otherUser = new User(2L, "otheruser", "other@example.com", "password");
        SongList otherPlaylist = new SongList(PLAYLIST_ID, "Other Playlist", false, otherUser);
        
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(otherPlaylist);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.getPlaylistById(TOKEN, PLAYLIST_ID)
        );
        assertEquals("User does not have access to this playlist", exception.getMessage());
    }

    @Test
    @DisplayName("Share playlist successfully")
    void testSharePlaylistSuccess() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(testPlaylist);
        
        // Act
        playlistService.sharePlaylist(PLAYLIST_ID, TOKEN);
        
        // Assert
        assertTrue(testPlaylist.getIsPublic());
        verify(songListRepository).save(testPlaylist);
    }

    @Test
    @DisplayName("Share playlist fails when user does not exist")
    void testSharePlaylistFailsWhenUserDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.sharePlaylist(PLAYLIST_ID, TOKEN)
        );
        assertEquals("User not logged in", exception.getMessage());
        verify(songListRepository, never()).save(any());
    }

    @Test
    @DisplayName("Share playlist fails when playlist does not exist")
    void testSharePlaylistFailsWhenPlaylistDoesNotExist() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);
        when(songListRepository.findById(PLAYLIST_ID)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            playlistService.sharePlaylist(PLAYLIST_ID, TOKEN)
        );
        assertEquals("Playlist does not exist", exception.getMessage());
        verify(songListRepository, never()).save(any());
    }
}