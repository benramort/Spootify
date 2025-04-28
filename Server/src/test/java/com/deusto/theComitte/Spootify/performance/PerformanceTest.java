package com.deusto.theComitte.Spootify.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;


@ExtendWith(JUnitPerfInterceptor.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT)
public class PerformanceTest {

    private static AtomicInteger counter = new AtomicInteger(2);
    
        @JUnitPerfTestActiveConfig
        private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
                .reportGenerator(new HtmlReportGenerator( "target/site/performance.html"))
                .build();

    @BeforeAll
    public static void cleanDatabase() {

        // Database connection details
        String jdbcUrl = "jdbc:mysql://database-test:3306/spootifydb";
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

    @BeforeAll
    public static void setUp() throws Exception {
        //Create the user before attempting login
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"user5\", \"email\":\"user5@user5\", \"password\":\"password\"}"))
                .build();
        

        HttpClient.newHttpClient().send(createUserRequest, HttpResponse.BodyHandlers.ofString());

    }

    @Test
     @JUnitPerfTest(threads = 10, durationMs = 5000)
     @JUnitPerfTestRequirement(meanLatency = 100)
     public void testLogin() {
        try{
         System.out.println("Running testLogin");

          //Perform login
         HttpRequest loginRequest = HttpRequest.newBuilder()
                 .uri(new URI("http://localhost:8081/login"))
                 .header("Content-Type", "application/json")
                 .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"user5@user5\", \"password\":\"password\"}"))
                 .build();

         // Assuming you have a method to convert the request body to UserDTO
         HttpResponse<String> loginResponse = HttpClient.newHttpClient().send(loginRequest, HttpResponse.BodyHandlers.ofString());
         assertEquals(200, loginResponse.statusCode());
         assertNotNull(loginResponse.body());
         assertTrue(loginResponse.body().contains("token"));
        }catch (Exception e) {
            
            e.printStackTrace();
            System.err.println("Failed to login: " + e.getMessage());
            fail();
        }
     }

     @Test
     @JUnitPerfTest(threads = 10, durationMs = 5000)
     @JUnitPerfTestRequirement(meanLatency = 100)
     public void testSearchArtist() {
        try {
            // Ensure the user is created before running this test
         System.out.println("Running testSearchArtist");

         HttpRequest searchRequest = HttpRequest.newBuilder()
                 .uri(new URI("http://localhost:8081/artists/search?name=artist"))
                 .header("Content-Type", "application/json")
                 .GET()
                 .build();

         HttpResponse<String> searchResponse = HttpClient.newHttpClient().send(searchRequest, HttpResponse.BodyHandlers.ofString());
         assertEquals(200, searchResponse.statusCode());
         assertNotNull(searchResponse.body());

         System.out.println(searchResponse.body());

         List<ArtistDTO> artists = new ObjectMapper().readValue(searchResponse.body(), new TypeReference<List<ArtistDTO>>() {});
            assertEquals(1, artists.size());
            assertEquals("artist", artists.get(0).getName());

         

         


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to search artist: " + e.getMessage());
            fail();
        }
    }

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000)
    @JUnitPerfTestRequirement(meanLatency = 100)
    public void testCreateArtist(){
        try{
            HttpRequest createArtistRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/artists"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"artist2\", \"email\":\"artist" + counter.getAndIncrement() + "@artist\", \"password\":\"password\"}"))
                .build();

            HttpResponse<String> artistResponse =  HttpClient.newHttpClient().send(createArtistRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, artistResponse.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create artist: " + e.getMessage());
            fail();
        }
    }
}



