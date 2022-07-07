package com.cribl.logcollection.controllers;

import com.cribl.logcollection.pojo.ErrorResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LogCollectionControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    static MockWebServer mockWebServer;
    static int remoteMachinePort;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        remoteMachinePort = mockWebServer.getPort();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public void testGetWholeFile() throws Exception {
        Map<String, List<String>> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out", Map.class);
        assertThat(response.get("master")).contains("-- End of daily output --");
    }

    @Test
    public void testGet10Records() throws Exception {
        Map<String, List<String>> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out?last_n=10", Map.class);
        assertThat(response.get("master")).hasSize(10);
    }

    @Test
    public void testGet10RecordsFilterBy() throws Exception {
        Map<String, List<String>> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out?last_n=10&filter_by=utu", Map.class);
        assertThat(response.get("master")).hasSize(10);
    }

    @Test
    public void testGetInvalidFile() throws Exception {
        ErrorResponse errorResponse = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily1.out?last_n=10&filter_by=utu", ErrorResponse.class);
        assertThat(errorResponse.getErrorMessage()).isEqualTo("file: daily1.out not found");
    }

    @Test
    public void testGetWholeFileFromRemoteMachine() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"slave1\":[\"-- End of daily output --\",\"22:09  up 12 days,  8:01, 1 user, load averages: 4.33 4.04 3.66\",\"Local system status:\",\"utun3      1400  fdfa:6a1c:c fdfa:6a1c:c7b7:de 21037780     -  7362246     -     -\",\"utun3      1400  10.101.101.15 amswinhs2x562.p 21037780     -  7362246     -     -\",\"utun3      1400  m-c02drjmam fe80:11::aede:48f 21037780     -  7362246     -     -\",\"utun3      1400  <Link#17>                     21037780     0  7362246     0     0\",\"utun2      1000  m-c02drjmam fe80:10::ce81:b1c        0     -     1413     -     -\",\"utun2      1000  <Link#16>                            0     0     1413     0     0\",\"utun1      2000  m-c02drjmam fe80:f::8972:d2ef        0     -     1413     -     -\",\"utun1      2000  <Link#15>                            0     0     1413     0     0\",\"utun0      1380  m-c02drjmam fe80:e::add0:987f        0     -     1413     -     -\",\"utun0      1380  <Link#14>                            0     0     1413     0     0\",\"bridge0    1500  <Link#13>   82:37:88:03:90:01        0     0        0     0     0\",\"en4        1500  <Link#12>   82:37:88:03:90:04        0     0        0     0     0\",\"en2        1500  <Link#11>   82:37:88:03:90:00        0     0        0     0     0\",\"en1        1500  <Link#10>   82:37:88:03:90:01        0     0        0     0     0\",\"en3        1500  <Link#9>    82:37:88:03:90:05        0     0        0     0     0\",\"llw0       1500  fe80::98f1: fe80:8::98f1:61ff        0     -        0     -     -\",\"llw0       1500  <Link#8>    9a:f1:61:f7:4b:03        0     0        0     0     0\",\"awdl0      1500  fe80::98f1: fe80:7::98f1:61ff     1744     -     1958     -     -\",\"awdl0      1500  <Link#7>    9a:f1:61:f7:4b:03     1744     0     1958     0     0\",\"en0        1500  2601:646:8b 2601:646:8b00:1a0 29553091     - 10812868     -     -\",\"en0        1500  2601:646:8b 2601:646:8b00:1a0 29553091     - 10812868     -     -\",\"en0        1500  2601:646:8b 2601:646:8b00:1a0 29553091     - 10812868     -     -\",\"en0        1500  10/24         10.0.0.25       29553091     - 10812868     -     -\",\"en0        1500  m-c02drjmam fe80:6::1c52:a294 29553091     - 10812868     -     -\",\"en0        1500  <Link#6>    14:7d:da:d6:2c:12 29553091     0 10812868     0     0\",\"ap1*       1500  <Link#5>    36:7d:da:d6:2c:12        0     0        0     0     0\",\"en5        1500  m-c02drjmam fe80:4::aede:48ff   129003     -   128655     -     -\",\"en5        1500  <Link#4>    ac:de:48:00:11:22   129003     0   128655  1435     0\",\"stf0*      1280  <Link#3>                             0     0        0     0     0\",\"gif0*      1280  <Link#2>                             0     0        0     0     0\",\"lo0        16384 m-c02drjmam fe80:1::1         23021131     - 23021131     -     -\",\"lo0        16384 localhost   ::1               23021131     - 23021131     -     -\",\"lo0        16384 127           localhost       23021131     - 23021131     -     -\",\"lo0        16384 <Link#1>                      23021131     0 23021131     0     0\",\"Name       Mtu   Network       Address            Ipkts Ierrs    Opkts Oerrs  Coll\",\"Network interface status:\",\"/dev/disk1s2    466Gi  290Gi  153Gi    66% 1186877 1606665120    0%   /System/Volumes/Data\",\"/dev/disk1s6    466Gi  3.9Mi  153Gi     1%      18 1606665120    0%   /System/Volumes/Update\",\"/dev/disk1s3    466Gi  325Mi  153Gi     1%    1287 1606665120    0%   /System/Volumes/Preboot\",\"/dev/disk1s5    466Gi  7.0Gi  153Gi     5%       7 1606665120    0%   /System/Volumes/VM\",\"/dev/disk1s1s1  466Gi   14Gi  153Gi     9%  500637 1606665120    0%   /\",\"Filesystem       Size   Used  Avail Capacity iused      ifree %iused  Mounted on\",\"Disk status:\",\"Removing stale files from /var/rwho:\",\"Cleaning out old system announcements:\",\"Removing old temporary files:\",\"Wed Jul  6 22:09:17 PDT 2022\"]}"));
        Map<String, List<String>> response = this.restTemplate.getForObject("http://localhost:" + port + "/get_logs/daily.out?remote_machines=http://localhost:" + remoteMachinePort, Map.class);
        assertThat(response.get("slave1")).isNotNull();
        assertThat(response.get("slave1")).hasSize(50);
    }
}
