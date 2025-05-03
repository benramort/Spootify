package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.TokenDTO;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.User;
import com.deusto.theComitte.Spootify.service.ArtistService;
import com.deusto.theComitte.Spootify.service.SongService;
import com.deusto.theComitte.Spootify.service.UserService;

public class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @Mock
    private UserService userService;

    @Mock
    private SongService songService;

    @InjectMocks
    private ArtistController artistController;

    private final long TOKEN = 12345L;
    private final long ARTIST_ID = 1L;
    private final long USER_ID = 2L;

    private Artist testArtist;
    private Album testAlbum;
    private Song testSong;
    private User testUser;
    private ArtistDTO testArtistDTO;
    private SongDTO testSongDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test artist
        testArtist = new Artist(ARTIST_ID, "Test Artist", "artist@example.com", "password");
        
        // Create test album
        testAlbum = new Album(1L, "Test Album");
        
        // Create test song
        testSong = new Song(1L, "Test Song", testAlbum, 180, "https://youtube.com/watch?v=test");
        
        // Create test user
        testUser = new User(USER_ID, "Test User", "user@example.com", "password");
        
        // Set up relationships
        List<Artist> albumArtists = new ArrayList<>();
        albumArtists.add(testArtist);
        testAlbum.setArtists(albumArtists);
        
        List<Album> artistAlbums = new ArrayList<>();
        artistAlbums.add(testAlbum);
        testArtist.setAlbums(artistAlbums);
        
        List<Song> albumSongs = new ArrayList<>();
        albumSongs.add(testSong);
        testAlbum.setSongs(albumSongs);
        
        List<User> artistFollowers = new ArrayList<>();
        artistFollowers.add(testUser);
        testArtist.setFollowersList(artistFollowers);
        
        // Create DTOs - we'll use real methods instead of mocking them
        testArtistDTO = new Artist(ARTIST_ID, "Test Artist", "artist@example.com", "password").toDTO();
        testSongDTO = new SongDTO(1L, "Test Song", 180, "https://youtube.com/watch?v=test");
    }

    @Test
    @DisplayName("Create artist successfully")
    void testCreateArtistSuccess() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("New Artist", "new@example.com", "password");
        doNothing().when(artistService).createArtist("New Artist", "new@example.com", "password");

        // Act
        ResponseEntity<Void> response = artistController.createArtist(createUserDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(artistService).createArtist("New Artist", "new@example.com", "password");
    }

    @Test
    @DisplayName("Create artist fails when artist already exists")
    void testCreateArtistFailsWhenArtistAlreadyExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("Existing Artist", "existing@example.com", "password");
        doThrow(new RuntimeException("Artist already exists"))
            .when(artistService).createArtist("Existing Artist", "existing@example.com", "password");

        // Act
        ResponseEntity<Void> response = artistController.createArtist(createUserDTO);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(artistService).createArtist("Existing Artist", "existing@example.com", "password");
    }

    @Test
    @DisplayName("Create artist fails with general error")
    void testCreateArtistFailsWithGeneralError() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("Error Artist", "error@example.com", "password");
        doThrow(new RuntimeException("General error"))
            .when(artistService).createArtist("Error Artist", "error@example.com", "password");

        // Act
        ResponseEntity<Void> response = artistController.createArtist(createUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).createArtist("Error Artist", "error@example.com", "password");
    }

    @Test
    @DisplayName("Login successfully")
    void testLoginSuccess() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("artist@example.com", "password");
        when(artistService.login("artist@example.com", "password")).thenReturn(TOKEN);
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);

        // Act
        ResponseEntity<TokenDTO> response = artistController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TOKEN, response.getBody().token());
        assertEquals(ARTIST_ID, response.getBody().id());
        verify(artistService).login("artist@example.com", "password");
        verify(artistService).getActiveArtist(TOKEN);
    }

    @Test
    @DisplayName("Login fails when artist does not exist")
    void testLoginFailsWhenArtistDoesNotExist() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("nonexistent@example.com", "password");
        when(artistService.login("nonexistent@example.com", "password"))
            .thenThrow(new RuntimeException("Artist does not exist"));

        // Act
        ResponseEntity<TokenDTO> response = artistController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(artistService).login("nonexistent@example.com", "password");
    }

    @Test
    @DisplayName("Login fails with incorrect password")
    void testLoginFailsWithIncorrectPassword() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("artist@example.com", "wrongpassword");
        when(artistService.login("artist@example.com", "wrongpassword"))
            .thenThrow(new RuntimeException("Incorrect password"));

        // Act
        ResponseEntity<TokenDTO> response = artistController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(artistService).login("artist@example.com", "wrongpassword");
    }

    @Test
    @DisplayName("Login fails with general error")
    void testLoginFailsWithGeneralError() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("artist@example.com", "password");
        doThrow(new RuntimeException("General error"))
            .when(artistService).login("artist@example.com", "password");
        // Act
        ResponseEntity<TokenDTO> response = artistController.login(loginDTO);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).login("artist@example.com", "password");
    }

    @Test
    @DisplayName("Logout successfully")
    void testLogoutSuccess() {
        // Arrange
        doNothing().when(artistService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = artistController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(artistService).logout(TOKEN);
    }

    @Test
    @DisplayName("Logout fails when artist not logged in")
    void testLogoutFailsWhenArtistNotLoggedIn() {
        // Arrange
        doThrow(new RuntimeException("Artist not logged in")).when(artistService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = artistController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(artistService).logout(TOKEN);
    }

    @Test
    @DisplayName("Logout fails with general error")
    void testLogoutFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error")).when(artistService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = artistController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).logout(TOKEN);
    }

    @Test
    @DisplayName("Get my songs successfully")
    void testGetMySongsSuccess() {
        // Arrange
        List<Song> songs = Arrays.asList(testSong);
        when(songService.getArtistSongs(TOKEN)).thenReturn(songs);

        // Act
        ResponseEntity<List<SongDTO>> response = artistController.getMySongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Song", response.getBody().get(0).getTitle());
        verify(songService).getArtistSongs(TOKEN);
    }

    @Test
    @DisplayName("Get my songs fails when artist not logged in")
    void testGetMySongsFailsWhenArtistNotLoggedIn() {
        // Arrange
        when(songService.getArtistSongs(TOKEN))
            .thenThrow(new RuntimeException("Artist not logged in"));

        // Act
        ResponseEntity<List<SongDTO>> response = artistController.getMySongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(songService).getArtistSongs(TOKEN);
    }

    @Test
    @DisplayName("Get my songs fails with general error")
    void testGetMySongsFailsWithGeneralError() {
        // Arrange
        when(songService.getArtistSongs(TOKEN))
            .thenThrow(new RuntimeException("General error"));

        // Act
        ResponseEntity<List<SongDTO>> response = artistController.getMySongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(songService).getArtistSongs(TOKEN);
    }

    @Test
    @DisplayName("Get my profile successfully")
    void testGetMyProfileSuccess() {
        // Arrange
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ARTIST_ID, response.getBody().getId());
        assertEquals("Test Artist", response.getBody().getName());
        verify(artistService).getActiveArtist(TOKEN);
    }

    @Test
    @DisplayName("Get my profile fails when artist not logged in")
    void testGetMyProfileFailsWhenArtistNotLoggedIn() {
        // Arrange
        when(artistService.getActiveArtist(TOKEN))
            .thenThrow(new RuntimeException("Artist not logged in"));

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(artistService).getActiveArtist(TOKEN);
    }

    @Test
    @DisplayName("Get my profile fails with general error")
    void testGetMyProfileFailsWithGeneralError() {
        // Arrange
        when(artistService.getActiveArtist(TOKEN))
            .thenThrow(new RuntimeException("General error"));

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).getActiveArtist(TOKEN);
    }

    @Test
    @DisplayName("Get all artists successfully")
    void testGetArtistsSuccess() {
        // Arrange
        List<Artist> artists = Arrays.asList(testArtist);
        when(artistService.getArtists()).thenReturn(artists);

        // Act
        ResponseEntity<List<ArtistDTO>> response = artistController.getArtists();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ARTIST_ID, response.getBody().get(0).getId());
        assertEquals("Test Artist", response.getBody().get(0).getName());
        verify(artistService).getArtists();
    }

    @Test
    @DisplayName("Get all artists fails with exception")
    void testGetArtistsFailsWithException() {
        // Arrange
        when(artistService.getArtists())
            .thenThrow(new RuntimeException("Error getting artists"));

        // Act
        ResponseEntity<List<ArtistDTO>> response = artistController.getArtists();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).getArtists();
    }

    @Test
    @DisplayName("Follow artist successfully")
    void testFollowArtistSuccess() {
        // Arrange
        doNothing().when(userService).followArtist(TOKEN, ARTIST_ID);

        // Act
        ResponseEntity<Void> response = artistController.followArtist(TOKEN, ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).followArtist(TOKEN, ARTIST_ID);
    }

    @Test
    @DisplayName("Follow artist fails when user not logged in")
    void testFollowArtistFailsWhenUserNotLoggedIn() {
        // Arrange
        doThrow(new RuntimeException("User not logged in"))
            .when(userService).followArtist(TOKEN, ARTIST_ID);

        // Act
        ResponseEntity<Void> response = artistController.followArtist(TOKEN, ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService).followArtist(TOKEN, ARTIST_ID);
    }

    @Test
    @DisplayName("Follow artist fails when artist does not exist")
    void testFollowArtistFailsWhenArtistDoesNotExist() {
        // Arrange
        doThrow(new RuntimeException("Artist does not exist"))
            .when(userService).followArtist(TOKEN, ARTIST_ID);

        // Act
        ResponseEntity<Void> response = artistController.followArtist(TOKEN, ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).followArtist(TOKEN, ARTIST_ID);
    }

    @Test
    @DisplayName("Follow artist fails with general error")
    void testFollowArtistFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(userService).followArtist(TOKEN, ARTIST_ID);

        // Act
        ResponseEntity<Void> response = artistController.followArtist(TOKEN, ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).followArtist(TOKEN, ARTIST_ID);
    }

    @Test
    @DisplayName("Get artist by ID successfully")
    void testGetArtistByIdSuccess() {
        // Arrange
        when(artistService.getArtist(ARTIST_ID)).thenReturn(testArtist);
        // Create a modified DTO that includes followers

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getArtist(ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ARTIST_ID, response.getBody().getId());
        assertEquals("Test Artist", response.getBody().getName());
        assertEquals(1, response.getBody().getFollowersList().size());
        assertEquals(USER_ID, response.getBody().getFollowersList().get(0).getId());
        verify(artistService).getArtist(ARTIST_ID);
    }

    @Test
    @DisplayName("Get artist by ID fails when artist does not exist")
    void testGetArtistByIdFailsWhenArtistDoesNotExist() {
        // Arrange
        when(artistService.getArtist(ARTIST_ID))
            .thenThrow(new RuntimeException("Artist does not exist"));

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getArtist(ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(artistService).getArtist(ARTIST_ID);
    }

    @Test
    @DisplayName("Get artist by ID fails with general error")
    void testGetArtistByIdFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(artistService).getArtist(ARTIST_ID);

        // Act
        ResponseEntity<ArtistDTO> response = artistController.getArtist(ARTIST_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).getArtist(ARTIST_ID);
    }

    @Test 
    @DisplayName("get artist by name successfully")
    void testSearchArtistsByNameSuccess() {
        
        String name = "Paco";
        Artist pacoArtist = new Artist(ARTIST_ID, "Paco", "paco@example.com", "password");
        List<Artist> artists = Arrays.asList(pacoArtist);
        when(artistService.searchArtists(name)).thenReturn(artists);
    

        
        ResponseEntity<List<ArtistDTO>> response = artistController.searchArtists(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ARTIST_ID, response.getBody().get(0).getId());
        assertEquals("Paco", response.getBody().get(0).getName());
        verify(artistService).searchArtists(name);
    }

    @Test
    @DisplayName("get artist by name fail")
    void testSearchArtistsByNameFail() {

        String name = "Paco";
        when(artistService.searchArtists(name)).thenThrow(new RuntimeException("No artists found with the given name"));

        ResponseEntity<List<ArtistDTO>> response = artistController.searchArtists(name);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(artistService).searchArtists(name);
    }

    @Test
    @DisplayName("Search artist with invalid name")
    void testSearchArtistsInvalidName() {

        String invalidName = ""; 
        when(artistService.searchArtists(invalidName))
            .thenThrow(new RuntimeException("Invalid input"));

        ResponseEntity<List<ArtistDTO>> response = artistController.searchArtists(invalidName);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(artistService).searchArtists(invalidName);
    }
}