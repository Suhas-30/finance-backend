package com.financeapp.dashboard.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.record.domain.Record;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.repository.RecordRepository;
import com.financeapp.user.domain.User;
import com.financeapp.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private Authentication authentication;

    @Test
    void shouldReturnSummarySuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");

        Record r1 = new Record();
        r1.setType(RecordType.INCOME);
        r1.setAmount(100.0);

        Record r2 = new Record();
        r2.setType(RecordType.EXPENSE);
        r2.setAmount(40.0);

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(recordRepository.findAll()).thenReturn(List.of(r1, r2));

        DashboardSummaryResponse response = dashboardService.getSummary(authentication);

        Assertions.assertEquals(100.0, response.getTotalIncome(), 0.001);
        Assertions.assertEquals(40.0, response.getTotalExpense(), 0.001);
        Assertions.assertEquals(60.0, response.getNetBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenUserNotFoundInSummary() {
        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            dashboardService.getSummary(authentication);
        });
    }

    @Test
    void shouldReturnRecentRecords() {
        User user = new User();
        user.setEmail("test@gmail.com");

        Record record = new Record();
        record.setCreatedBy(user);
        record.setCreatedAt(LocalDateTime.now());

        when(recordRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(record));

        var result = dashboardService.getRecentRecords();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void shouldReturnCategoryBreakdown() {
        Record r1 = new Record();
        r1.setCategory("Food");
        r1.setAmount(100.0);

        Record r2 = new Record();
        r2.setCategory("Food");
        r2.setAmount(50.0);

        when(recordRepository.findAll()).thenReturn(List.of(r1, r2));

        List<CategoryBreakdownResponse> result = dashboardService.getCategoryBreakdown();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(150.0, result.get(0).getTotalAmount(), 0.001);
    }

    @Test
    void shouldReturnTrends() {
        Record r1 = new Record();
        r1.setDate(LocalDate.of(2025, 1, 10));
        r1.setAmount(100.0);

        Record r2 = new Record();
        r2.setDate(LocalDate.of(2025, 1, 20));
        r2.setAmount(50.0);

        when(recordRepository.findAll()).thenReturn(List.of(r1, r2));

        List<TrendResponse> result = dashboardService.getTrends();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(150.0, result.get(0).getTotalAmount(), 0.001);
    }
}