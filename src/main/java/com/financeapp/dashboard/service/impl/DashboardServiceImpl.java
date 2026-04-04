package com.financeapp.dashboard.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.dashboard.dto.CategoryBreakdownResponse;
import com.financeapp.dashboard.dto.DashboardSummaryResponse;
import com.financeapp.dashboard.dto.TrendResponse;
import com.financeapp.dashboard.service.DashboardService;
import com.financeapp.record.dto.RecordResponse;
import com.financeapp.record.repository.RecordRepository;
import com.financeapp.record.domain.Record;
import com.financeapp.record.domain.RecordType;
import com.financeapp.user.domain.User;
import com.financeapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    @Override
    public DashboardSummaryResponse getSummary(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        List<Record> records = recordRepository.findAll();

        double totalIncome = records.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .mapToDouble(Record::getAmount)
                .sum();

        double totalExpense = records.stream()
                .filter(r -> r.getType() == RecordType.EXPENSE)
                .mapToDouble(Record::getAmount)
                .sum();

        double netBalance = totalIncome - totalExpense;

        return  new DashboardSummaryResponse(totalIncome, totalExpense, netBalance);

    }

    @Override
    public List<RecordResponse> getRecentRecords() {
        List<Record> records = recordRepository.findTop5ByOrderByCreatedAtDesc();
        return records.stream().map(record -> {
            RecordResponse res = new RecordResponse();
            res.setId(record.getId());
            res.setAmount(record.getAmount());
            res.setType(record.getType());
            res.setCategory(record.getCategory());
            res.setDate(record.getDate());
            res.setNote(record.getNote());
            res.setCreatedBy(record.getCreatedBy().getEmail());
            res.setCreatedAt(record.getCreatedAt());
            return res;
        }).toList();

    }

    @Override
    public List<CategoryBreakdownResponse> getCategoryBreakdown() {
        List<Record> records = recordRepository.findAll();


        Map<String, Double> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.summingDouble(Record::getAmount)
                ));

        return grouped.entrySet().stream()
                .map(entry -> new CategoryBreakdownResponse(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    @Override
    public List<TrendResponse> getTrends() {


        List<Record> records = recordRepository.findAll();

        Map<String, Double> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getDate().getYear() + "-" +
                                String.format("%02d", record.getDate().getMonthValue()),
                        Collectors.summingDouble(Record::getAmount)
                ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendResponse(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

}
