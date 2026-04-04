package com.financeapp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryBreakdownResponse {

    private String category;
    private Double totalAmount;
}