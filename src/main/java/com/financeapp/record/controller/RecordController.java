package com.financeapp.record.controller;

import com.financeapp.common.response.ApiResponse;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.dto.RecordResponse;
import com.financeapp.record.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> createRecords(@Valid @RequestBody RecordRequest request, Authentication authentication){
        recordService.createRecord(request, authentication);
        return new ApiResponse<>(true, "Record created successfully", null);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getAllRecords(Authentication authentication){
        List<RecordResponse> records = recordService.getAllRecords(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Records fetached successfully", records));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable UUID id,  Authentication authentication){
        RecordResponse response = recordService.getRecordById(id, authentication);

        return ResponseEntity.ok(new ApiResponse<>(true, "Record fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateRecordById(@PathVariable UUID id, @RequestBody RecordRequest request, Authentication authentication){
        recordService.updateRecord(id, request, authentication);

        return  ResponseEntity.ok(new ApiResponse<>(true, "Record updated successfully", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable UUID id, Authentication authentication){
        recordService.deleteRecord(id, authentication);

        return ResponseEntity.ok(new ApiResponse<>(true, "Record deleted successfully", null));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<ApiResponse<List<RecordResponse>> > getAllRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {

        List<RecordResponse> records = recordService.getAllRecords(type, category, date, authentication);

        return ResponseEntity.ok(new ApiResponse<>(true, "Records fetched successfully", records));
    }

}
