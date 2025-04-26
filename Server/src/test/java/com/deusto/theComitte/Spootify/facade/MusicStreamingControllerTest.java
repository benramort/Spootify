package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deusto.theComitte.Spootify.service.MusicStreamingService;

public class MusicStreamingControllerTest {

    @Mock
    private MusicStreamingService musicStreamingService;

    @InjectMocks
    private MusicStreamingController musicStreamingController;

    private final String VALID_SONG_ID = "1";
    private final String INVALID_SONG_ID = "abc";
    private final String SONG_PATH = "/path/to/song.mp3";
    private final long START = 0;
    private final long END = 1024 * 1024 - 1; // 1MB chunk size

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Stream music successfully")
    void testStreamMusicSuccess() throws IOException {
        // Arrange
        Resource mockResource = mock(Resource.class);
        when(mockResource.contentLength()).thenReturn(2 * 1024 * 1024L); // 2MB
        when(musicStreamingService.getSongPath(Long.parseLong(VALID_SONG_ID))).thenReturn(SONG_PATH);
        when(musicStreamingService.getFullResource(SONG_PATH)).thenReturn(mockResource);
        when(musicStreamingService.getChunk(SONG_PATH, START, END)).thenReturn(mockResource);

        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(null, VALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bytes", response.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES));
        verify(musicStreamingService).getSongPath(Long.parseLong(VALID_SONG_ID));
        verify(musicStreamingService).getFullResource(SONG_PATH);
        verify(musicStreamingService).getChunk(SONG_PATH, START, END);
    }

    @Test
    @DisplayName("Stream music fails with BAD_REQUEST when songId is invalid")
    void testStreamMusicFailsWithBadRequest() {
        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(null, INVALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(musicStreamingService, never()).getSongPath(anyLong());
    }

    @Test
    @DisplayName("Stream music fails with NOT_FOUND when song does not exist")
    void testStreamMusicFailsWithNotFound() {
        // Arrange
        when(musicStreamingService.getSongPath(Long.parseLong(VALID_SONG_ID)))
            .thenThrow(new RuntimeException("Song not found"));

        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(null, VALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(musicStreamingService).getSongPath(Long.parseLong(VALID_SONG_ID));
    }

    @Test
    @DisplayName("Stream music fails with INTERNAL_SERVER_ERROR when IOException occurs")
    void testStreamMusicFailsWithInternalServerError() throws IOException {
        // Arrange
        when(musicStreamingService.getSongPath(Long.parseLong(VALID_SONG_ID))).thenReturn(SONG_PATH);
        when(musicStreamingService.getFullResource(SONG_PATH)).thenThrow(new IOException("File read error"));

        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(null, VALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(musicStreamingService).getSongPath(Long.parseLong(VALID_SONG_ID));
        verify(musicStreamingService).getFullResource(SONG_PATH);
    }

    @Test
    @DisplayName("Stream music with range header")
    void testStreamMusicWithRangeHeader() throws IOException {
        // Arrange
        String rangeHeader = "bytes=0-1023";
        Resource mockResource = mock(Resource.class);
        when(mockResource.contentLength()).thenReturn(2 * 1024 * 1024L); // 2MB
        when(musicStreamingService.getSongPath(Long.parseLong(VALID_SONG_ID))).thenReturn(SONG_PATH);
        when(musicStreamingService.getFullResource(SONG_PATH)).thenReturn(mockResource);
        when(musicStreamingService.getChunk(SONG_PATH, START, 1023)).thenReturn(mockResource);

        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(rangeHeader, VALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bytes", response.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES));
        assertEquals("bytes 0-1023/2097152", response.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));
        verify(musicStreamingService).getSongPath(Long.parseLong(VALID_SONG_ID));
        verify(musicStreamingService).getFullResource(SONG_PATH);
        verify(musicStreamingService).getChunk(SONG_PATH, START, 1023);
    }

    @Test
    @DisplayName("Stream music calculates end correctly when contentLength is smaller than MAX_CHUNK_SIZE")
    void testStreamMusicCalculatesEndCorrectly() throws IOException {
        // Arrange
        String rangeHeader = "bytes=0-";
        long contentLength = 512 * 1024; // 512 KB, menor que MAX_CHUNK_SIZE
        Resource mockResource = mock(Resource.class);

        when(mockResource.contentLength()).thenReturn(contentLength);
        when(musicStreamingService.getSongPath(Long.parseLong(VALID_SONG_ID))).thenReturn(SONG_PATH);
        when(musicStreamingService.getFullResource(SONG_PATH)).thenReturn(mockResource);
        when(musicStreamingService.getChunk(SONG_PATH, 0, contentLength - 1)).thenReturn(mockResource);

        // Act
        ResponseEntity<Resource> response = musicStreamingController.streamMusic(rangeHeader, VALID_SONG_ID);

        // Assert
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bytes", response.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES));
        assertEquals("bytes 0-524287/524288", response.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE)); // 524287 = 512 KB - 1
        verify(musicStreamingService).getChunk(SONG_PATH, 0, contentLength - 1);
    }
}
