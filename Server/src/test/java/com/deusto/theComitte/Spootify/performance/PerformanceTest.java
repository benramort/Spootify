package com.deusto.theComitte.Spootify.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.Rule;
import org.junit.jupiter.api.Test;


import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

public class PerformanceTest {
    
    @Rule 
	public JUnitPerfRule perfTestRule = new JUnitPerfRule(new HtmlReportGenerator("target/junitperf/report.html"));

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000)
    @JUnitPerfTestRequirement(meanLatency = 100)
    public void testLogin() throws Exception {
        System.out.println("Running testLogin");

        // Create the user before attempting login
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"user1\", \"email\":\"user1@user1\", \"password\":\"password\"}"))
                .build();

        HttpResponse<String> createUserResponse = HttpClient.newHttpClient().send(createUserRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createUserResponse.statusCode()); // Assuming 201 Created for user registration

        // Perform login
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"user1\", \"email\":\"user1@user1\", \"password\":\"password\"}"))
                .build();

        HttpResponse<String> loginResponse = HttpClient.newHttpClient().send(loginRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginResponse.statusCode());
        assertNotNull(loginResponse.body());
        assertTrue(loginResponse.body().contains("token"));
    }

}



