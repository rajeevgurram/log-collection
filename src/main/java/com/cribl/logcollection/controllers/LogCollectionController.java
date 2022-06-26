package com.cribl.logcollection.controllers;

import com.cribl.logcollection.service.LogCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/get_logs")
public class LogCollectionController {
    private final LogCollectionService service;

    @Autowired
    public LogCollectionController(final LogCollectionService service) {
        this.service = service;
    }

    @GetMapping(value = "/{file_name}")
    public CompletableFuture<List<String>> getLogFileData(@PathVariable("file_name") final String fileName,
                                                          @RequestParam("last_n") final Optional<Integer> lastN,
                                                          @RequestParam(value = "filter_by", required = false) final String filterBy) {
        return service.getLogs(fileName, lastN.orElse(Integer.MAX_VALUE), filterBy);
    }
}
