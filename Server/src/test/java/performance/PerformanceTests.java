package performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.Rule;
import org.junit.Test;


import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

public class PerformanceTests {
    
    @Rule 
	public JUnitPerfRule perfTestRule = new JUnitPerfRule(new HtmlReportGenerator("target/junitperf/report.html"));

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000)
    @JUnitPerfTestRequirement(meanLatency = 100)
    public void testLogin() throws Exception {
        System.out.println("Running testLogin");
        HttpRequest request= HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"user1\", \"email\":\"user1@user1\", \"password\":\"password\"}"))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertTrue(response.body().contains("token"));
    }

}



