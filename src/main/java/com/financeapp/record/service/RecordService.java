package com.financeapp.record.service;

import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.dto.RecordResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RecordService {

     void createRecord(RecordRequest request, Authentication authentication);

     List<RecordResponse> getAllRecords(Authentication authentication);

     RecordResponse getRecordById(UUID id, Authentication authentication);

     void updateRecord(UUID id, RecordRequest request, Authentication authentication);

     void deleteRecord(UUID id, Authentication authentication);

     List<RecordResponse> getAllRecords(RecordType type, String category, LocalDate date, Authentication authentication);
}
