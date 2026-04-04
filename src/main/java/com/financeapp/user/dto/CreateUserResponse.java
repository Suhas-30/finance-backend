package com.financeapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Setter
@Getter
@AllArgsConstructor
public class CreateUserResponse {
    private String tempPassword;
    private String message;
}
