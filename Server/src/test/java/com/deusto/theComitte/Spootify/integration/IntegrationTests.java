package com.deusto.theComitte.Spootify.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTests {

    private HttpClient client;
    private ObjectMapper objectMapper;

    private static long ARTIST_ID;
    private long ALBUM_ID;
    private static long SONG_ID;

    private static String jdbcUrl;

    @Autowired
    public void setEnvironment(Environment env) {
        jdbcUrl = env.getProperty("spring.datasource.url");
        System.out.println("Initialized JDBC URL: " + jdbcUrl);
    }

    @BeforeAll
    public static void cleanDatabase() {

        System.out.println("JDBC url: " + jdbcUrl);

        // Database connection details
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement statement = connection.createStatement()) {
            
            // Temporarily disable foreign key checks
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            
            // Delete data from all tables in the correct order
            String[] deleteStatements = {
                "DELETE FROM song_list_songs",    // Junction table first
                "DELETE FROM song_lists",         // Then parent tables
                "DELETE FROM songs",
                "DELETE FROM artist_followers",   // Another junction table
                "DELETE FROM users",
                "DELETE FROM artists",
                "DELETE FROM albums"
            };
            
            for (String sql : deleteStatements) {
                try {
                    statement.executeUpdate(sql);
                    System.out.println("Executed: " + sql);
                } catch (Exception e) {
                    System.out.println("Warning: " + e.getMessage() + " for query: " + sql);
                    // Continue with other statements even if one fails
                }
            }
            
            // Reset auto-increment counters for all tables
            String[] resetAutoIncrementStatements = {
                "ALTER TABLE song_lists AUTO_INCREMENT = 1",
                "ALTER TABLE songs AUTO_INCREMENT = 1",
                "ALTER TABLE users AUTO_INCREMENT = 1",
                "ALTER TABLE artists AUTO_INCREMENT = 1",
                "ALTER TABLE albums AUTO_INCREMENT = 1"
            };
            
            for (String sql : resetAutoIncrementStatements) {
                try {
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    System.out.println("Warning: " + e.getMessage() + " for query: " + sql);
                }
            }
            
            // Re-enable foreign key checks
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("Database cleaned successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to clean the database: " + e.getMessage());
        }
    }

    @BeforeEach
    public void setup() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void integrationTest1() { //Crear artista --> Iniciar sesión --> Crear álbum --> Crear canción
        try {
            String NAME = "artist1";
            String EMAIL = "artist1@artist1";
            String PASSWORD = "artist1";

            // Crear artista

            CreateUserDTO user = new CreateUserDTO(NAME, EMAIL, PASSWORD);
        
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/artists"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(user)))
            .header("Content-Type", "application/json")
            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            // Iniciar sesión

            LoginDTO login = new LoginDTO(EMAIL, PASSWORD);
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/artists/login"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(login)))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            long TOKEN = objectMapper.readTree(response.body()).get("token").asLong();
            ARTIST_ID = objectMapper.readTree(response.body()).get("id").asLong();
            System.out.println("Token: " + TOKEN);

            // Crear álbum
            String ALBUM_NAME = "album1";
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String CRLF = "\r\n";
            StringBuilder requestBody = new StringBuilder();

            // Add album name part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"name\"").append(CRLF);
            requestBody.append(CRLF); // Empty line after headers
            requestBody.append(ALBUM_NAME).append(CRLF);

            // Add token part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"token\"").append(CRLF);
            requestBody.append(CRLF); // Empty line after headers
            requestBody.append(TOKEN).append(CRLF);

            // End the multipart request
            requestBody.append("--").append(boundary).append("--").append(CRLF);

            // Build the request
            request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/albums"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            System.out.println("Body: " + response.body());


            // Comprobar que el álbum se ha creado correctamente
            request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/albums?artist=" + ARTIST_ID))
                .GET()
                .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<AlbumDTO> albums = objectMapper.readValue(response.body(), new TypeReference<List<AlbumDTO>>() {});
            assertEquals(1, albums.size());
            assertEquals(ALBUM_NAME, albums.get(0).getName());
            ALBUM_ID = albums.get(0).getId();

            // Crear canción
            String SONG_NAME = "Test Song";
            int DURATION = 180; // 3 minutes
            
            boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            CRLF = "\r\n";
            requestBody = new StringBuilder();
            
            // Add title part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"title\"").append(CRLF);
            requestBody.append(CRLF);
            requestBody.append(SONG_NAME).append(CRLF);
            
            // Add album part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"album\"").append(CRLF);
            requestBody.append(CRLF);
            requestBody.append(ALBUM_ID).append(CRLF);
            
            // Add duration part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"duration\"").append(CRLF);
            requestBody.append(CRLF);
            requestBody.append(DURATION).append(CRLF);
            
            // Add token part
            requestBody.append("--").append(boundary).append(CRLF);
            requestBody.append("Content-Disposition: form-data; name=\"token\"").append(CRLF);
            requestBody.append(CRLF);
            requestBody.append(TOKEN).append(CRLF);
            
            // End the multipart request
            requestBody.append("--").append(boundary).append("--").append(CRLF);
            
            // Build the request
            request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/songs"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
            
            response = client.send(request, BodyHandlers.ofString());
            
            // Verify response
            assertEquals(200, response.statusCode(), "Failed to create song: " + response.body());
            System.out.println("Song created successfully");

            //Comprobar que la canción se ha creado correctamente
            request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/songs?artistId=" + ARTIST_ID + "&albumId=" + ALBUM_ID))
                .GET()
                .build();
            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Failed to get songs: " + response.body());
            List<SongDTO> songs = objectMapper.readValue(response.body(), new TypeReference<List<SongDTO>>() {});
            assertEquals(1, songs.size());
            assertEquals(SONG_NAME, songs.get(0).getTitle());
            assertEquals(DURATION, songs.get(0).getDuration());
            assertEquals(ALBUM_ID, songs.get(0).getAlbum().getId());

            SONG_ID = songs.get(0).getId();
            System.out.println("Song ID: " + SONG_ID);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            fail();
            
        }
        
    }

    @Test
    public void integrationTest2() { ////Crear usuario --> Iniciar sesión --> Crear playlist --> Añadir canción a playlist
        try {
            String NAME = "user1";
            String EMAIL = "user1@user1";
            String PASSWORD = "user1";

            // Crear usuario
            CreateUserDTO user = new CreateUserDTO(NAME, EMAIL, PASSWORD);
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/users"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(user)))
            .header("Content-Type", "application/json")
            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            // Iniciar sesión
            LoginDTO login = new LoginDTO(EMAIL, PASSWORD);
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/login"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(login)))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            long TOKEN = objectMapper.readTree(response.body()).get("token").asLong();
            long USER_ID = objectMapper.readTree(response.body()).get("id").asLong();

            //Crear playlist
            String PLAYLIST_NAME = "playlist1";
            SongListDTO songListDTO = new SongListDTO(PLAYLIST_NAME, true);
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/playlists?token=" + TOKEN))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(songListDTO)))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            //Obtener id de la playlist
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/playlists?token=" + TOKEN))
            .GET()
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SongListDTO> playlists = objectMapper.readValue(response.body(), new TypeReference<List<SongListDTO>>() {});
            assertEquals(2, playlists.size());
            assertEquals(PLAYLIST_NAME, playlists.get(1).getName());
            long PLAYLIST_ID = playlists.get(1).getId();

            // Añadir canción a la playlist
            SongDTO song = new SongDTO();
            song.setId(SONG_ID);
            System.out.println("Song ID: " + SONG_ID);

            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/playlists/" + PLAYLIST_ID + "/songs?token=" + TOKEN))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(song)))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Failed to add song to playlist: " + response.body());
            
            // Comprobar que la canción se ha añadido a la playlist
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/playlists/" + PLAYLIST_ID + "?token=" + TOKEN))
            .GET()
            .build();

            response = client.send(request, BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Failed to get playlist: " + response.body());
            SongListDTO playlist = objectMapper.readValue(response.body(), SongListDTO.class);
            assertEquals(1, playlist.getSongs().size());
            assertEquals(SONG_ID, playlist.getSongs().get(0).getId());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
            
        }
    }

    //Crear usuario --> Iniciar sesión --> Seguir artista
    @Test
    public void integrationTest3(){
        try{

            



            String NAME = "user2";
            String EMAIL = "user2@user2";
            String PASSWORD = "user2";

            // Crear usuario
            CreateUserDTO user = new CreateUserDTO(NAME, EMAIL, PASSWORD);
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/users"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(user)))
            .header("Content-Type", "application/json")
            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            // Iniciar sesión
            LoginDTO login = new LoginDTO(EMAIL, PASSWORD);
            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/login"))
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(login)))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            long TOKEN = objectMapper.readTree(response.body()).get("token").asLong();
            long USER_ID = objectMapper.readTree(response.body()).get("id").asLong();

            // Seguir artista
            System.out.println("ARTIST_ID: " + ARTIST_ID);

            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/artists/" + ARTIST_ID + "/followers?token=" + TOKEN))
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .header("Content-Type", "application/json")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            System.out.println("Response: " + response.body());
            
            request  = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/artists/" + ARTIST_ID + "?token=" + TOKEN))
            .GET()
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Failed to get artist: " + response.body());

            ArtistDTO artist = objectMapper.readValue(response.body(), ArtistDTO.class);
            assertEquals(1, artist.getFollowersList().size());
            assertEquals(USER_ID, artist.getFollowersList().get(0).getId());


            System.out.println("Hola");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
            
        }

    }
    
}
