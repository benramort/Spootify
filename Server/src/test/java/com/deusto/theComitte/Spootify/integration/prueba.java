package com.deusto.theComitte.Spootify.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class prueba {

    private HttpClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    public void pruebita() {
        assertTrue(true);
    }


    public void integrationTest1() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/songs"))
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.body());
            
            List<SongDTO> responseObjects = objectMapper.readValue(response.body(), new TypeReference<List<SongDTO>>() {});
            System.out.println(responseObjects.get(0).getId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
}
