package com.financeapp.record.dto;

import com.financeapp.record.domain.RecordType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RecordResponse {

    private UUID id;

    private Double amount;

    private RecordType type;

    private String category;

    private LocalDate date;

    private String note;

    private String createdBy;

    private LocalDateTime createdAt;
}