package com.cribl.logcollection.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LogCollectionService {
    CompletableFuture<List<String>> getLogs(final String fileName, final Integer lastN, final String filterBy);
}
