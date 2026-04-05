package com.financeapp.record.controller;

import com.financeapp.common.response.ApiResponse;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.dto.RecordResponse;
import com.financeapp.record.service.RecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Record APIs", description = "Income and Expense management endpoints")
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(
            summary = "Create record",
            description = "Creates a new income or expense record (Role: ADMIN)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> createRecords(@Valid @RequestBody RecordRequest request, Authentication authentication){
        recordService.createRecord(request, authentication);
        return new ApiResponse<>(true, "Record created successfully", null);
    }

    @Operation(
            summary = "Get all records",
            description = "Fetch all records for logged-in user (Roles: ADMIN, ANALYST)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Records fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getAllRecords(Authentication authentication){
        List<RecordResponse> records = recordService.getAllRecords(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Records fetched successfully", records));
    }

    @Operation(
            summary = "Get record by ID",
            description = "Fetch a specific record using its ID (Roles: ADMIN, ANALYST)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Record not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable UUID id, Authentication authentication){
        RecordResponse response = recordService.getRecordById(id, authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Record fetched successfully", response));
    }

    @Operation(
            summary = "Update record",
            description = "Updates an existing record by ID (Role: ADMIN)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateRecordById(@PathVariable UUID id,
                                                              @RequestBody RecordRequest request,
                                                              Authentication authentication){
        recordService.updateRecord(id, request, authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Record updated successfully", null));
    }

    @Operation(
            summary = "Delete record",
            description = "Deletes a record by ID (Role: ADMIN)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable UUID id, Authentication authentication){
        recordService.deleteRecord(id, authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Record deleted successfully", null));
    }

    @Operation(
            summary = "Filter records",
            description = "Filter records by type, category, or date (Roles: ADMIN, ANALYST)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Filtered records fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getAllRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {

        List<RecordResponse> records = recordService.getAllRecords(type, category, date, authentication);

        return ResponseEntity.ok(new ApiResponse<>(true, "Records fetched successfully", records));
    }
}