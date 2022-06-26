package com.cribl.logcollection.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    final int httpStatus;
    final boolean error;
    final String errorMessage;
}
