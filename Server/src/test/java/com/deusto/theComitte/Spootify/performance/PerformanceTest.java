package com.deusto.theComitte.Spootify.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class PerformanceTest {
    
        @JUnitPerfTestActiveConfig
        private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
                .reportGenerator(new HtmlReportGenerator(System.getProperty("user.dir") + "performance.html"))
                .build();

    @BeforeEach
    public void cleanDatabase() {
        // Database connection details
        String jdbcUrl = "jdbc:mysql://localhost:3306/spootifydb"; // Replace with your DB URL
        String username = "root"; // Replace with your DB username
        String password = "root"; // Replace with your DB password

        // SQL to clean the database
        String deleteSongListsSql = "DELETE FROM song_lists"; // Adjust table name as needed
        String deleteUsersSql = "DELETE FROM users"; // Adjust table name as needed
        String deleteArtistsSql = "DELETE FROM artists";
        String resetAutoIncrementUsersSql = "ALTER TABLE users AUTO_INCREMENT = 1"; // Optional: Reset auto-increment
        String resetAutoIncrementSongListsSql = "ALTER TABLE song_lists AUTO_INCREMENT = 1"; // Optional: Reset auto-increment
        String resetAutoIncrementArtistsSql = "ALTER TABLE artists AUTO_INCREMENT = 1";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {
            // Delete rows from child tables first
            statement.executeUpdate(deleteSongListsSql);

            // Delete rows from parent table
            statement.executeUpdate(deleteUsersSql);
            statement.executeUpdate(deleteArtistsSql);

            // Reset auto-increment counters (optional)
            statement.executeUpdate(resetAutoIncrementUsersSql);
            statement.executeUpdate(resetAutoIncrementSongListsSql);
            statement.executeUpdate(resetAutoIncrementArtistsSql);

            System.out.println("Database cleaned successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to clean the database: " + e.getMessage());
        }
    }

    public void setUp() throws Exception {
        //Create the user before attempting login
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"user5\", \"email\":\"user5@user5\", \"password\":\"password\"}"))
                .build();

        HttpRequest createArtistRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/artists"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"artist\", \"email\":\"artist@artist\", \"password\":\"password\"}"))
                .build();

        HttpResponse<String> createUserResponse = HttpClient.newHttpClient().send(createUserRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createUserResponse.statusCode()); // Assuming 201 Created for user registration
        HttpResponse<String> createArtistResponse = HttpClient.newHttpClient().send(createArtistRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createArtistResponse.statusCode()); // Assuming 201 Created for artist registration
    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000)
    @JUnitPerfTestRequirement(meanLatency = 100)
    public void prueba() {
        System.out.println("Running test");
        assertTrue(true);
    }

////    @Test
//     @JUnitPerfTest(threads = 10, durationMs = 5000)
//     @JUnitPerfTestRequirement(meanLatency = 100)
//     public void testLogin() throws Exception {
//         System.out.println("Running testLogin");

//         // Perform login
//         HttpRequest loginRequest = HttpRequest.newBuilder()
//                 .uri(new URI("http://localhost:8081/login"))
//                 .header("Content-Type", "application/json")
//                 .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"user5@user5\", \"password\":\"password\"}"))
//                 .build();

//         HttpResponse<String> loginResponse = HttpClient.newHttpClient().send(loginRequest, HttpResponse.BodyHandlers.ofString());
//         assertEquals(200, loginResponse.statusCode());
//         assertNotNull(loginResponse.body());
//         assertTrue(loginResponse.body().contains("token"));
//     }

//     @Test@JUnitPerfTest(threads = 10, durationMs = 5000)
//     @JUnitPerfTestRequirement(meanLatency = 100)
//     public void testSearchArtist() throws Exception {
//         System.out.println("Running testSearchArtist");

//         HttpRequest searchRequest = HttpRequest.newBuilder()
//                 .uri(new URI("http://localhost:8081/artists/search?name=artist"))
//                 .header("Content-Type", "application/json")
//                 .GET()
//                 .build();

//         HttpResponse<String> searchResponse = HttpClient.newHttpClient().send(searchRequest, HttpResponse.BodyHandlers.ofString());
//         assertEquals(200, searchResponse.statusCode());
//         assertNotNull(searchResponse.body());
//         assertTrue(searchResponse.body().contains("artist"));

//     }

//     @Test
//     @JUnitPerfTest(threads = 10, durationMs = 5000)
//     @JUnitPerfTestRequirement(meanLatency = 100)
//     public void testFollowArtist() throws Exception {
//         System.out.println("Running testFollowArtist");

//         // Perform login to get the token
//         HttpRequest loginRequest = HttpRequest.newBuilder()
//                 .uri(new URI("http://localhost:8081/login"))
//                 .header("Content-Type", "application/json")
//                 .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"user5@user5\", \"password\":\"password\"}"))
//                 .build();

//         HttpResponse<String> loginResponse = HttpClient.newHttpClient().send(loginRequest, HttpResponse.BodyHandlers.ofString());
//         assertEquals(200, loginResponse.statusCode());
//         String token = loginResponse.body().split(":")[1].replace("\"", "").replace("}", "").trim(); 

//         // Follow artist
//         HttpRequest followRequest = HttpRequest.newBuilder()
//                 .uri(new URI("http://localhost:8081/artists/follow/1")) 
//                 .header("Authorization", "Bearer " + token)
//                 .header("Content-Type", "application/json")
//                 .POST(HttpRequest.BodyPublishers.ofString("{}"))
//                 .build();

//         HttpResponse<String> followResponse = HttpClient.newHttpClient().send(followRequest, HttpResponse.BodyHandlers.ofString());
//         assertEquals(200, followResponse.statusCode());
//     }

}



