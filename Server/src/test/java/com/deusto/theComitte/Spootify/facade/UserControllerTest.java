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

import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.DTO.TokenDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;
import com.deusto.theComitte.Spootify.service.UserService;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final long TOKEN = 12345L;
    private final long USER_ID = 1L;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User(USER_ID, "Test User", "user@example.com", "password");
        
        // Set up relationships
        List<Artist> followedArtists = new ArrayList<>();
        Artist testArtist = new Artist(1L, "Test Artist", "artist@example.com", "password");
        followedArtists.add(testArtist);
        
        List<SongList> playlists = new ArrayList<>();
        SongList testPlaylist = new SongList(1L, "Test Playlist", true, testUser);
        playlists.add(testPlaylist);
        testUser.setSongLists(playlists);
    }

    @Test
    @DisplayName("Create user successfully")
    void testCreateUserSuccess() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("New User", "new@example.com", "password");
        doNothing().when(userService).createUser("New User", "new@example.com", "password");

        // Act
        ResponseEntity<Void> response = userController.createUser(createUserDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).createUser("New User", "new@example.com", "password");
    }

    @Test
    @DisplayName("Create user fails when user already exists")
    void testCreateUserFailsWhenUserAlreadyExists() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("Existing User", "existing@example.com", "password");
        doThrow(new RuntimeException("User already exists"))
            .when(userService).createUser("Existing User", "existing@example.com", "password");

        // Act
        ResponseEntity<Void> response = userController.createUser(createUserDTO);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userService).createUser("Existing User", "existing@example.com", "password");
    }

    @Test
    @DisplayName("Create user fails with general error")
    void testCreateUserFailsWithGeneralError() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("Error User", "error@example.com", "password");
        doThrow(new RuntimeException("General error"))
            .when(userService).createUser("Error User", "error@example.com", "password");

        // Act
        ResponseEntity<Void> response = userController.createUser(createUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).createUser("Error User", "error@example.com", "password");
    }

    @Test
    @DisplayName("Login successfully")
    void testLoginSuccess() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
        when(userService.login("user@example.com", "password")).thenReturn(TOKEN);
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);

        // Act
        ResponseEntity<TokenDTO> response = userController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TOKEN, response.getBody().token());
        assertEquals(USER_ID, response.getBody().id());
        verify(userService).login("user@example.com", "password");
        verify(userService).getActiveUser(TOKEN);
    }

    @Test
    @DisplayName("Login fails when user does not exist")
    void testLoginFailsWhenUserDoesNotExist() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("nonexistent@example.com", "password");
        when(userService.login("nonexistent@example.com", "password"))
            .thenThrow(new RuntimeException("User does not exist"));

        // Act
        ResponseEntity<TokenDTO> response = userController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).login("nonexistent@example.com", "password");
    }

    @Test
    @DisplayName("Login fails with incorrect password")
    void testLoginFailsWithIncorrectPassword() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("user@example.com", "wrongpassword");
        when(userService.login("user@example.com", "wrongpassword"))
            .thenThrow(new RuntimeException("Incorrect password"));

        // Act
        ResponseEntity<TokenDTO> response = userController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService).login("user@example.com", "wrongpassword");
    }

    @Test
    @DisplayName("Login fails with general error")
    void testLoginFailsWithGeneralError() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
        when(userService.login("user@example.com", "password"))
            .thenThrow(new RuntimeException("General error"));

        // Act
        ResponseEntity<TokenDTO> response = userController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).login("user@example.com", "password");
    }

    @Test
    @DisplayName("Logout successfully")
    void testLogoutSuccess() {
        // Arrange
        doNothing().when(userService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = userController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).logout(TOKEN);
    }

    @Test
    @DisplayName("Logout fails when user not logged in")
    void testLogoutFailsWhenUserNotLoggedIn() {
        // Arrange
        doThrow(new RuntimeException("User not logged in")).when(userService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = userController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService).logout(TOKEN);
    }

    @Test
    @DisplayName("Logout fails with general error")
    void testLogoutFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error")).when(userService).logout(TOKEN);

        // Act
        ResponseEntity<Void> response = userController.logout(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).logout(TOKEN);
    }

    @Test
    @DisplayName("Get all users successfully")
    void testGetUsersSuccess() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(USER_ID, response.getBody().get(0).getId());
        assertEquals("Test User", response.getBody().get(0).getName());
        verify(userService).getUsers();
    }

    @Test
    @DisplayName("Get all users fails with general error")
    void testGetUsersFailsWithGeneralError() {
        // Arrange
        when(userService.getUsers()).thenThrow(new RuntimeException("Error getting users"));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsers();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).getUsers();
    }

    @Test
    @DisplayName("Get my profile successfully")
    void testGetMyProfileSuccess() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenReturn(testUser);

        // Act
        ResponseEntity<UserDTO> response = userController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(USER_ID, response.getBody().getId());
        assertEquals("Test User", response.getBody().getName());
        verify(userService).getActiveUser(TOKEN);
    }

    @Test
    @DisplayName("Get my profile fails when user not logged in")
    void testGetMyProfileFailsWhenUserNotLoggedIn() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenThrow(new RuntimeException("User not logged in"));

        // Act
        ResponseEntity<UserDTO> response = userController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService).getActiveUser(TOKEN);
    }

    @Test
    @DisplayName("Get my profile fails with general error")
    void testGetMyProfileFailsWithGeneralError() {
        // Arrange
        when(userService.getActiveUser(TOKEN)).thenThrow(new RuntimeException("General error"));

        // Act
        ResponseEntity<UserDTO> response = userController.getMyProfile(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).getActiveUser(TOKEN);
    }

    @Test
    @DisplayName("Get liked songs successfully")
    void testGetLikedSongsSuccess() {
        // Arrange
        SongList likedSongs = new SongList("Liked Songs", false, testUser);
        when(userService.getLikedSongs(TOKEN)).thenReturn(likedSongs);

        // Act
        ResponseEntity<SongListDTO> response = userController.getLikedSongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(likedSongs.toDTO().getId(), response.getBody().getId());
        verify(userService).getLikedSongs(TOKEN);
    }

    @Test
    @DisplayName("Get liked songs fails when user not logged in")
    void testGetLikedSongsFailsWhenUserNotLoggedIn() {
        // Arrange
        when(userService.getLikedSongs(TOKEN)).thenThrow(new RuntimeException("User not logged in"));

        // Act
        ResponseEntity<SongListDTO> response = userController.getLikedSongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService).getLikedSongs(TOKEN);
    }

    @Test
    @DisplayName("Get liked songs fails when song list does not exist")
    void testGetLikedSongsFailsWhenSongListDoesNotExist() {
        // Arrange
        when(userService.getLikedSongs(TOKEN)).thenThrow(new RuntimeException("Song list does not exist"));

        // Act
        ResponseEntity<SongListDTO> response = userController.getLikedSongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getLikedSongs(TOKEN);
    }

    @Test
    @DisplayName("Get liked songs fails with general error")
    void testGetLikedSongsFailsWithGeneralError() {
        // Arrange
        when(userService.getLikedSongs(TOKEN)).thenThrow(new RuntimeException("General error"));

        // Act
        ResponseEntity<SongListDTO> response = userController.getLikedSongs(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).getLikedSongs(TOKEN);
    }
}