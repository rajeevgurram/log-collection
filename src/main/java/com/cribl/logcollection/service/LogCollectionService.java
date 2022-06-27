package com.cribl.logcollection.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface LogCollectionService {
    CompletableFuture<Map<String, List<String>>> getLogs(final String fileName,
                                                         final Integer lastN,
                                                         final String filterBy,
                                                         final String[] remoteMachines);
}
