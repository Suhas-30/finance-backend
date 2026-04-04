package com.financeapp.dashboard.service;

import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.record.dto.RecordResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface DashboardService {
     DashboardSummaryResponse getSummary(Authentication authentication);
     List<RecordResponse> getRecentRecords();
     List<CategoryBreakdownResponse> getCategoryBreakdown();
     List<TrendResponse> getTrends();
}
