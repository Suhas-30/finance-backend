package com.financeapp.record.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeapp.config.jwt.JwtAuthFilter;
import com.financeapp.config.jwt.JwtService;
import com.financeapp.config.ratelimit.filter.RateLimitFilter;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.dto.RecordResponse;
import com.financeapp.record.service.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = RecordController.class,
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
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRecordSuccessfully() throws Exception {
        RecordRequest request = new RecordRequest();
        request.setAmount(100.0);
        request.setType(RecordType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        doNothing().when(recordService).createRecord(any(), any());

        mockMvc.perform(post("/api/v1/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Record created successfully"));
    }

    @Test
    void shouldGetAllRecordsSuccessfully() throws Exception {
        RecordResponse response = new RecordResponse();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        when(recordService.getAllRecords(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/record")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldGetRecordByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        RecordResponse response = new RecordResponse();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        when(recordService.getRecordById(eq(id), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/record/" + id)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldUpdateRecordSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        RecordRequest request = new RecordRequest();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        doNothing().when(recordService).updateRecord(eq(id), any(), any());

        mockMvc.perform(put("/api/v1/record/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldDeleteRecordSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        doNothing().when(recordService).deleteRecord(eq(id), any());

        mockMvc.perform(delete("/api/v1/record/" + id)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldFilterRecordsSuccessfully() throws Exception {
        RecordResponse response = new RecordResponse();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        when(recordService.getAllRecords(any(), any(), any(), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/record/filter")
                        .param("type", "INCOME")
                        .param("category", "Salary")
                        .param("date", LocalDate.now().toString())
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }
}