package com.financeapp.record.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.record.repository.RecordRepository;
import com.financeapp.record.domain.Record;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.dto.RecordResponse;
import com.financeapp.record.service.RecordService;
import com.financeapp.user.domain.Role;
import com.financeapp.user.domain.User;
import com.financeapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    @Override
    public void createRecord(RecordRequest request, Authentication authentication) {
        String  email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        Record record = new Record();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());

        record.setCreatedBy(user);
        record.setUpdatedBy(user);
        recordRepository.save(record);

    }

    @Override
    public List<RecordResponse> getAllRecords(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()->new AppException("User not found", HttpStatus.NOT_FOUND));

        List<Record> records = recordRepository.findAll();

        return  records.stream().map(record -> {
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
        } ).toList();
    }

    @Override
    public RecordResponse getRecordById(UUID id, Authentication authentication) {
        Record record = recordRepository.findById(id).orElseThrow(()-> new AppException("Record not found", HttpStatus.NOT_FOUND));

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

    }

    @Override
    public void updateRecord(UUID id, RecordRequest request, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        Record record = recordRepository.findById(id).orElseThrow(()-> new AppException("Record not found", HttpStatus.NOT_FOUND));
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());
        record.setUpdatedBy(user);
        recordRepository.save(record);
    }


    @Override
    public void deleteRecord(UUID id, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new AppException("User not found", HttpStatus.NOT_FOUND));

        if(user.getRole() != Role.ADMIN){
            throw  new AppException("Access Denied", HttpStatus.FORBIDDEN);
        }

        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException("Record not found", HttpStatus.NOT_FOUND));

        recordRepository.delete(record);

    }

    @Override
    public List<RecordResponse> getAllRecords(RecordType type, String category, LocalDate date, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        List<Record> records = recordRepository.findAll();
        Stream<Record> stream = records.stream();

        if (type != null) {
            stream = stream.filter(r -> r.getType().equals(type));
        }

        if (category != null && !category.isBlank()) {
            stream = stream.filter(r -> r.getCategory().equalsIgnoreCase(category));
        }

        if (date != null) {
            stream = stream.filter(r -> r.getDate().equals(date));
        }

        return stream.map(record -> {
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
}
