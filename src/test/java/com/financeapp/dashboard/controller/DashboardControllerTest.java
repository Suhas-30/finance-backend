package com.financeapp.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeapp.config.jwt.JwtAuthFilter;
import com.financeapp.config.jwt.JwtService;
import com.financeapp.config.ratelimit.filter.RateLimitFilter;
import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.dashboard.service.DashboardService;
import com.financeapp.record.dto.RecordResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = DashboardController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthFilter.class,
                                RateLimitFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetSummarySuccessfully() throws Exception {
        DashboardSummaryResponse response =
                new DashboardSummaryResponse(100.0, 40.0, 60.0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        when(dashboardService.getSummary(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/summary")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.netBalance").value(60.0));
    }

    @Test
    void shouldGetRecentRecordsSuccessfully() throws Exception {
        RecordResponse record = new RecordResponse();

        when(dashboardService.getRecentRecords()).thenReturn(List.of(record));

        mockMvc.perform(get("/api/v1/dashboard/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldGetCategoryBreakdownSuccessfully() throws Exception {
        CategoryBreakdownResponse response =
                new CategoryBreakdownResponse("Food", 150.0);

        when(dashboardService.getCategoryBreakdown())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/dashboard/category-breakdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].totalAmount").value(150.0));
    }

    @Test
    void shouldGetTrendsSuccessfully() throws Exception {
        TrendResponse response =
                new TrendResponse("2025-01", 150.0);

        when(dashboardService.getTrends())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/dashboard/trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }
}