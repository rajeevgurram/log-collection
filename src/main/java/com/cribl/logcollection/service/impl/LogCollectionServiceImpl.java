package com.cribl.logcollection.service.impl;

import com.cribl.logcollection.exceptions.LogFileNotFoundException;
import com.cribl.logcollection.service.LogCollectionService;
import com.cribl.logcollection.utils.ReverseFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LogCollectionServiceImpl implements LogCollectionService {
    @Override
    public CompletableFuture<List<String>> getLogs(final String fileName, final Integer lastN, final String filterBy) {
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
            return data;
        });
    }
}
