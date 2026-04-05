package com.financeapp.dashboard.controller;

import com.financeapp.common.response.ApiResponse;
import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.dashboard.service.DashboardService;
import com.financeapp.record.dto.RecordResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dashboard APIs", description = "Analytics and dashboard related endpoints")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get dashboard summary", description = "Returns total income, expense and balance")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(Authentication authentication){
        DashboardSummaryResponse response = dashboardService.getSummary(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Summary fetched successfully", response));
    }

    @Operation(summary = "Get recent records", description = "Fetches latest income and expense records")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recent records fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ApiResponse<List<RecordResponse>> getRecentRecords(){
        List<RecordResponse> records = dashboardService.getRecentRecords();
        return new ApiResponse<>(true, "Recent records fetched successfully", records);
    }

    @Operation(summary = "Get category breakdown", description = "Returns expense distribution by category")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category breakdown fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdown(){
        List<CategoryBreakdownResponse> response = dashboardService.getCategoryBreakdown();
        return ResponseEntity.ok(new ApiResponse<>(true, "Category breakdown fetched successfully", response));
    }

    @Operation(summary = "Get trends", description = "Returns income vs expense trends over time")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trends fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<ApiResponse<List<TrendResponse>>> getTrends(){
        List<TrendResponse> responses = dashboardService.getTrends();
        return ResponseEntity.ok(new ApiResponse<>(true, "Trends fetched successfully", responses));
    }
}