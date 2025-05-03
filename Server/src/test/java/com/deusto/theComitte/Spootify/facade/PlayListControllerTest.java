package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.service.PlaylistService;

public class PlayListControllerTest {

    @Mock
    private PlaylistService playlistService;

    @InjectMocks
    private PlayListController playListController;

    private final long TOKEN = 12345L;
    private final long PLAYLIST_ID = 1L;
    private final String PLAYLIST_NAME = "Test Playlist";
    private SongList testPlaylist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test playlist
        testPlaylist = new SongList(PLAYLIST_ID, PLAYLIST_NAME, null);
    }

    @Test
    @DisplayName("Create playlist successfully")
    void testCreatePlayListSuccess() {
        // Arrange
        SongListDTO songListDTO = new SongListDTO();
        songListDTO.setName(PLAYLIST_NAME);

        doNothing().when(playlistService).createPlayList(TOKEN, PLAYLIST_NAME);

        // Act
        ResponseEntity<Void> response = playListController.createPlayList(songListDTO, TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(playlistService).createPlayList(TOKEN, PLAYLIST_NAME);
    }

    @Test
    @DisplayName("Create playlist fails when user not logged in")
    void testCreatePlayListFailsWhenUserNotLoggedIn() {
        // Arrange
        SongListDTO songListDTO = new SongListDTO();
        songListDTO.setName(PLAYLIST_NAME);

        doThrow(new RuntimeException("User not logged in"))
            .when(playlistService).createPlayList(TOKEN, PLAYLIST_NAME);

        // Act
        ResponseEntity<Void> response = playListController.createPlayList(songListDTO, TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(playlistService).createPlayList(TOKEN, PLAYLIST_NAME);
    }

    // Removed duplicate method

    @Test
    @DisplayName("Get all playlists successfully")
    void testGetPlayListsSuccess() {
        // Arrange
        List<SongList> playlists = new ArrayList<>();
        playlists.add(testPlaylist);

        when(playlistService.getPlayLists(TOKEN)).thenReturn(playlists);

        // Act
        ResponseEntity<List<SongListDTO>> response = playListController.getPlayLists(TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(PLAYLIST_NAME, response.getBody().get(0).getName());
        verify(playlistService).getPlayLists(TOKEN);
    }

    @Test
    @DisplayName("Get all playlists fails when user not logged in")
    void testGetPlayListsFailsWhenUserNotLoggedIn() {
        // Arrange
        when(playlistService.getPlayLists(TOKEN))
            .thenThrow(new RuntimeException("User not logged in"));

        // Act
        ResponseEntity<List<SongListDTO>> response = playListController.getPlayLists(TOKEN);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(playlistService).getPlayLists(TOKEN);
    }

    @Test
    @DisplayName("Get playlist by ID successfully")
    void testGetPlaylistByIdSuccess() {
        // Arrange
        when(playlistService.getPlaylistById(TOKEN, PLAYLIST_ID)).thenReturn(testPlaylist);

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, PLAYLIST_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PLAYLIST_ID, response.getBody().getId());
        assertEquals(PLAYLIST_NAME, response.getBody().getName());
        verify(playlistService).getPlaylistById(TOKEN, PLAYLIST_ID);
    }

    @Test
    @DisplayName("Get playlist by ID fails when playlist does not exist")
    void testGetPlaylistByIdFailsWhenPlaylistDoesNotExist() {
        // Arrange
        when(playlistService.getPlaylistById(TOKEN, PLAYLIST_ID))
            .thenThrow(new RuntimeException("SongList does not exist"));

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, PLAYLIST_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(playlistService).getPlaylistById(TOKEN, PLAYLIST_ID);
    }

    @Test
    @DisplayName("Get playlist by ID fails when user not logged in")
    void testGetPlaylistByIdFailsWhenUserNotLoggedIn() {
        // Arrange
        when(playlistService.getPlaylistById(TOKEN, PLAYLIST_ID))
            .thenThrow(new RuntimeException("User not logged in"));

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, PLAYLIST_ID);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(playlistService).getPlaylistById(TOKEN, PLAYLIST_ID);
    }

    @Test
    @DisplayName("Add song to playlist successfully")
    void testAddSongToPlayListSuccess() {
        // Arrange
        SongDTO songDTO = new SongDTO();
        songDTO.setId(1L);

        doNothing().when(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);

        // Act
        ResponseEntity<Void> response = playListController.addSongToPlayList(TOKEN, PLAYLIST_ID, songDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);
    }

    @Test
    @DisplayName("Add song to playlist fails when user not logged in")
    void testAddSongToPlayListFailsWhenUserNotLoggedIn() {
        // Arrange
        SongDTO songDTO = new SongDTO();
        songDTO.setId(1L);

        doThrow(new RuntimeException("User not logged in"))
            .when(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);

        // Act
        ResponseEntity<Void> response = playListController.addSongToPlayList(TOKEN, PLAYLIST_ID, songDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);
    }

    @Test
    @DisplayName("Add song to playlist fails when playlist does not exist")
    void testAddSongToPlayListFailsWhenPlaylistDoesNotExist() {
        // Arrange
        SongDTO songDTO = new SongDTO();
        songDTO.setId(1L);

        doThrow(new RuntimeException("Playlist does not exist"))
            .when(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);

        // Act
        ResponseEntity<Void> response = playListController.addSongToPlayList(TOKEN, PLAYLIST_ID, songDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);
    }

    @Test
    @DisplayName("Create playlist fails when name is null")
    void testCreatePlayListFailsWhenNameIsNull() {
        // Arrange
        SongListDTO songListDTO = new SongListDTO();
        songListDTO.setName(null); // Nombre nulo

        // Act
        ResponseEntity<Void> response = playListController.createPlayList(songListDTO, TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(playlistService, never()).createPlayList(anyLong(), anyString());
    }

    @Test
    @DisplayName("Create playlist fails when name is empty")
    void testCreatePlayListFailsWhenNameIsEmpty() {
        // Arrange
        SongListDTO songListDTO = new SongListDTO();
        songListDTO.setName(""); // Nombre vacío

        // Act
        ResponseEntity<Void> response = playListController.createPlayList(songListDTO, TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(playlistService, never()).createPlayList(anyLong(), anyString());
    }

    @Test
    @DisplayName("Add song to playlist fails with general error")
    void testAddSongToPlayListFailsWithGeneralError() {
        // Arrange
        SongDTO songDTO = new SongDTO();
        songDTO.setId(1L);

        doThrow(new RuntimeException("General error"))
            .when(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);

        // Act
        ResponseEntity<Void> response = playListController.addSongToPlayList(TOKEN, PLAYLIST_ID, songDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(playlistService).addSongToPlayList(TOKEN, songDTO.getId(), PLAYLIST_ID);
    }

    @Test
    @DisplayName("Get playlists fails with general error")
    void testGetPlayListsFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(playlistService).getPlayLists(TOKEN);

        // Act
        ResponseEntity<?> response = playListController.getPlayLists(TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(playlistService).getPlayLists(TOKEN);
    }

    @Test
    @DisplayName("Get playlist by ID fails with general error")
    void testGetPlaylistByIdFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(playlistService).getPlaylistById(TOKEN, PLAYLIST_ID);

        // Act
        ResponseEntity<?> response = playListController.getPlaylistById(TOKEN, PLAYLIST_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(playlistService).getPlaylistById(TOKEN, PLAYLIST_ID);
    }

    @Test
    @DisplayName("Get playlist by ID fails with NOT_FOUND when playlist does not exist")
    void testGetPlaylistByIdFailsWithNotFound() {
        // Arrange
        long playlistId = 1L;

        // Simular que el servicio lanza una excepción indicando que la playlist no existe
        doThrow(new RuntimeException("SongList does not exist"))
            .when(playlistService).getPlaylistById(TOKEN, playlistId);

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, playlistId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(playlistService).getPlaylistById(TOKEN, playlistId);
    }

    @Test
    @DisplayName("Get playlist by ID fails with FORBIDDEN when user does not have access")
    void testGetPlaylistByIdFailsWithForbidden() {
        // Arrange
        long playlistId = 1L;

        // Simular que el servicio lanza una excepción indicando que el usuario no tiene acceso
        doThrow(new RuntimeException("User does not have access to this playlist"))
            .when(playlistService).getPlaylistById(TOKEN, playlistId);

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, playlistId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(playlistService).getPlaylistById(TOKEN, playlistId);
    }

    @Test
    @DisplayName("Get playlist by ID fails with NOT_FOUND when playlist is null")
    void testGetPlaylistByIdFailsWithNotFoundWhenPlaylistIsNull() {
        // Arrange
        long playlistId = 1L;

        // Simular que el servicio devuelve null para la playlist
        when(playlistService.getPlaylistById(TOKEN, playlistId)).thenReturn(null);

        // Act
        ResponseEntity<SongListDTO> response = playListController.getPlaylistById(TOKEN, playlistId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(playlistService).getPlaylistById(TOKEN, playlistId);
    }
}