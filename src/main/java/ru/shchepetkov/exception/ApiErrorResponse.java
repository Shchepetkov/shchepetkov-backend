package ru.shchepetkov.exception;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ApiErrorResponse {
    Instant timestamp;
    int status;
    String error;
    String code;
    String message;
    String path;
    Map<String, String> details;
}
