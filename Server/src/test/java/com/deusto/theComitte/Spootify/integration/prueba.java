package com.deusto.theComitte.Spootify.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.StreamingHttpOutputMessage.Body;

import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
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
            System.out.println("Token: " + TOKEN);

            // Crear álbum
            String ALBUM_NAME = "album1";

            request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/albums?"+"token="+TOKEN+"&name="+ALBUM_NAME))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "multipart/form-data")
            .build();

            response = client.send(request, BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            System.out.println("Body: " + response.body());





        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

    //Crear usuario --> Iniciar sesión --> Crear playlist --> Añadir canción a playlist

    //Crear usuario --> Iniciar sesión --> Dar like a canción --> Seguir artista
    
}
