package com.financeapp.common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;

    public ApiResponse(boolean status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
