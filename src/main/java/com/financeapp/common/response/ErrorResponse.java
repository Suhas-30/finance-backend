package com.financeapp.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Setter
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}