package com.cribl.logcollection.service.impl;

import com.cribl.logcollection.exceptions.LogFileNotFoundException;
import com.cribl.logcollection.service.LogCollectionService;
import com.cribl.logcollection.utils.ReverseFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogCollectionServiceImpl implements LogCollectionService {
    private final RestTemplate restTemplate;
    @Value("${server.name:master}")
    private String serverName;

    @Autowired
    public LogCollectionServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<Map<String, List<String>>> getLogs(final String fileName,
                                                   final Integer lastN,
                                                   final String filterBy,
                                                   final String[] remoteMachines) {
        if(remoteMachines != null && remoteMachines.length > 0) {
            return getLogsFromRemoteMachine(fileName, lastN, filterBy, remoteMachines);
        }

        log.info("Getting the {} from the machine: {}", fileName, serverName);
        return CompletableFuture.supplyAsync(() -> {
            final Path path = Paths.get("/var/log/" + fileName);
            if(!Files.exists(path)) {
                log.error("file: " + fileName + " not found");
                throw new LogFileNotFoundException(fileName);
            }
            return path;
        }).thenApply(path -> {
            final List<String> data = new ArrayList<>();
            ReverseFileReader reverseFileReader;
            int totalLines = 0;
            try {
                reverseFileReader = new ReverseFileReader(path.toFile());
                String line = "";
                while (totalLines < lastN && (line != null)) {
                    line = reverseFileReader.readLine();
                    if(line != null && !line.trim().equals("")) {
                        if(filterBy != null) {
                            if(!line.toLowerCase().contains(filterBy.toLowerCase())) {
                                continue;
                            }
                        }
                        data.add(line);
                        totalLines++;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(reverseFileReader != null) {
                reverseFileReader.close();
            }

            final Map<String, List<String>> response = new HashMap<>();
            response.put(serverName, data);
            return response;
        });
    }

    private CompletableFuture<Map<String, List<String>>> getLogsFromRemoteMachine(final String fileName,
                                                                                  final Integer lastN,
                                                                                  final String filterBy,
                                                                                  final String[] remoteMachines) {
        log.info("fetching data from remote machines: {}", remoteMachines);
        List<CompletableFuture<Map<String, List<String>>>> completableFutures = new ArrayList<>();

        for(String machine : remoteMachines) {
            completableFutures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    StringBuilder queryBuilder = new StringBuilder("?");
                    if(lastN != null) {
                        queryBuilder.append("last_n=").append(lastN).append("&");
                    }
                    if(filterBy != null) {
                        queryBuilder.append("filter_by=").append(filterBy);
                    }

                    return restTemplate.exchange(new URI(machine + "/get_logs/" + fileName + queryBuilder),
                            HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, List<String>>>(){})
                            .getBody();
                } catch (URISyntaxException e) {
                    log.error("Error occurred while fetching data from remote machine: {}", machine, e);
                    throw new RuntimeException(e);
                }
            }));
        }

        final Map<String, List<String>> response = new HashMap<>();
        return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .thenApply(unused -> {
                    return completableFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                })
                .thenApply(responses -> {
                    for(Map<String, List<String>> res : responses) {
                        String key = (String)res.keySet().toArray()[0];
                        response.put(key, res.get(key));
                    }
                    return response;
                });
    }
}
