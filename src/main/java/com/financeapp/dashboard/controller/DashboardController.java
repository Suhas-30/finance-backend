package com.financeapp.dashboard.controller;

import com.financeapp.common.response.ApiResponse;
import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.dashboard.service.DashboardService;
import com.financeapp.record.dto.RecordResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.from.TableReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(Authentication authentication){
        DashboardSummaryResponse response = dashboardService.getSummary(authentication);
        return  ResponseEntity.ok(new ApiResponse<>(true, "Summary fetched successfully", response));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ApiResponse<List<RecordResponse>> getRecentRecords(){

        List<RecordResponse> records = dashboardService.getRecentRecords();

        return new ApiResponse<>(true, "Recent records fetched successfully", records);
    }

    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdown(){

        List<CategoryBreakdownResponse> response =
                dashboardService.getCategoryBreakdown();

        return ResponseEntity.ok(new ApiResponse<>(true, "Category breakdown fetched successfully", response));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<ApiResponse<List<TrendResponse>>> getTrends(){
        List<TrendResponse> responses = dashboardService.getTrends();

        return  ResponseEntity.ok(new ApiResponse<>(true, "Trends fetched successfully", responses));
    }

}
