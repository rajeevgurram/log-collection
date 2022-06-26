package com.cribl.logcollection.controllers;

import com.cribl.logcollection.pojo.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LogCollectionControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetWholeFile() throws Exception {
        List<String> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out", List.class);
        assertThat(response.get(0)).contains("-- End of daily output --");
    }

    @Test
    public void testGet10Records() throws Exception {
        List<String> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out?last_n=10", List.class);
        assertThat(response.size()).isEqualTo(10);
    }

    @Test
    public void testGet10RecordsFilterBy() throws Exception {
        List<String> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out?last_n=10&filter_by=utu", List.class);
        assertThat(response.size()).isEqualTo(10);
    }

    @Test
    public void testGetInvalidFile() throws Exception {
        ErrorResponse errorResponse = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily1.out?last_n=10&filter_by=utu", ErrorResponse.class);
        assertThat(errorResponse.getErrorMessage()).isEqualTo("file: daily1.out not found");
    }
}
