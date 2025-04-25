package com.deusto.theComitte.Spootify.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.User;

public class UserServiceTest {
    
    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        String name = "User Name";
        String email = "user@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(null);
    
        userService.createUser(name, email, password);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_AlreadyExists() {
        String email = "user@example.com";
    
        when(userRepository.findByEmail(email)).thenReturn(new User());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser("User Name", email, "password");
        });

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        String email = "user@example.com";
        String password = "password";
        User user = new User(1L, "User Name", email, password);

        when(userRepository.findByEmail(email)).thenReturn(user);

        long token = userService.login(email, password);

        assertNotNull(token);
    }

    @Test
    void testLogin_UserDoesNotExist() {
        String email = "user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(email, "password");
        });

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        String email = "user@example.com";
        String password = "wrongPassword";
        User user = new User("User Name", email, "password");

        when(userRepository.findByEmail(email)).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            userService.login(email, password);
        });

        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    void testLogout_Success() {
        long token = 12345L;
        User user = new User(1L, "User Name", "user@example.com", "password");

        userService.getActiveUserMap().put(token, user);

        userService.logout(token);

        assertFalse(userService.getActiveUserMap().containsKey(token));


    }

    @Test
    void testLogout_UserNotLoggedIn() {
        long token = 12345L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.logout(token);
        });

        assertEquals("User not logged in", exception.getMessage());
    }

    @Test
    void testGetUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("User1", "user1@email.com", "password1"));
        users.add(new User("User2", "user2@email.com", "password2"));
        
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getName());
        assertEquals("User2", result.get(1).getName()); 
    }

    @Test
    void testGetActiveUser_Success() {
        long token = 12345L;
        User user = new User("User1", "user1@email.com", "password1");

        userService.getActiveUserMap().put(token, user);
        User result = userService.getActiveUser(token);
        assertEquals(user, result);
    }

    @Test
    void testGetActiveUser_UserNotLoggedIn() {
        long token = 12345L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getActiveUser(token);
        });

        assertEquals("User not logged in", exception.getMessage());
    }

    @Test
    void testFollowArtist_Success() {
        long token = 12345L;
        long artistID = 1L;
        User user = new User("User1", "user1@email.com", "password1");
        userService.getActiveUserMap().put(token, user);
        Artist artist = new Artist(artistID, "artist1", "artist1@email.com", "password1");
        
        when(artistRepository.findById(artistID)).thenReturn(artist);
        userService.followArtist(token, artistID);
        verify(artistRepository, times(1)).save(artist);
        verify(userRepository, times(1)).save(user);
        assertTrue(artist.getFollowersList().contains(user));
        assertTrue(user.getFollowList().contains(artist));
    }

    @Test
    void testFollowArtist_UserNotLoggedIn() {
        long token = 12345L;
        long artistID = 1L;
        Artist artist = new Artist(artistID, "artist1", "artist1@email.com", "password1");
        
        when(artistRepository.findById(artistID)).thenReturn(artist);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.followArtist(token, artistID);
        });
        assertEquals("User not logged in", exception.getMessage());
        verify(artistRepository, never()).save(any(Artist.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFollowArtist_ArtistDoesNotExist() {
        long token = 12345L;
        long artistID = 1L;
        User user = new User("User1", "user1@email.com", "password1");
        userService.getActiveUserMap().put(token, user);
        
        when(artistRepository.findById(artistID)).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.followArtist(token, artistID);
        });
        assertEquals("Artist does not exist", exception.getMessage());
        verify(artistRepository, never()).save(any(Artist.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
    
