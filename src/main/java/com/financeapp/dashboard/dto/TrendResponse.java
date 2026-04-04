package com.financeapp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrendResponse {

    private String month;
    private Double totalAmount;
}