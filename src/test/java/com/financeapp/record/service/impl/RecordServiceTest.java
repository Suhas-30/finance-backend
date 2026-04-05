package com.financeapp.record.service.impl;

import com.financeapp.common.exception.AppException;
import com.financeapp.record.domain.Record;
import com.financeapp.record.domain.RecordType;
import com.financeapp.record.dto.RecordRequest;
import com.financeapp.record.repository.RecordRepository;
import com.financeapp.record.service.impl.RecordServiceImpl;
import com.financeapp.user.domain.Role;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @InjectMocks
    private RecordServiceImpl recordService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Test
    void shouldCreateRecordSuccessfully() {
        RecordRequest request = new RecordRequest();
        request.setAmount(100.0);
        request.setType(RecordType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        request.setNote("Test");

        User user = new User();
        user.setEmail("test@gmail.com");

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        recordService.createRecord(request, authentication);

        verify(recordRepository, times(1)).save(any(Record.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundInCreate() {
        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            recordService.createRecord(new RecordRequest(), authentication);
        });
    }

    @Test
    void shouldGetAllRecordsSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");

        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setCreatedBy(user);

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(recordRepository.findAll()).thenReturn(List.of(record));

        List<?> result = recordService.getAllRecords(authentication);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenUserNotFoundInGetAll() {
        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            recordService.getAllRecords(authentication);
        });
    }

    @Test
    void shouldGetRecordByIdSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");

        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setCreatedBy(user);

        when(recordRepository.findById(record.getId())).thenReturn(Optional.of(record));

        var result = recordService.getRecordById(record.getId(), authentication);

        Assertions.assertNotNull(result);
    }

    @Test
    void shouldThrowWhenRecordNotFound() {
        UUID id = UUID.randomUUID();
        when(recordRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(AppException.class, () -> {
            recordService.getRecordById(id, authentication);
        });
    }

    @Test
    void shouldUpdateRecordSuccessfully() {
        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setRole(Role.ADMIN);

        Record record = new Record();
        UUID id = UUID.randomUUID();

        when(authentication.getName()).thenReturn("admin@gmail.com");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        when(recordRepository.findById(id)).thenReturn(Optional.of(record));

        recordService.updateRecord(id, new RecordRequest(), authentication);

        verify(recordRepository).save(record);
    }

    @Test
    void shouldThrowWhenNotAdminUpdate() {
        User user = new User();
        user.setRole(Role.VIEWER);

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        Assertions.assertThrows(AppException.class, () -> {
            recordService.updateRecord(UUID.randomUUID(), new RecordRequest(), authentication);
        });
    }

    @Test
    void shouldDeleteRecordSuccessfully() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        Record record = new Record();
        UUID id = UUID.randomUUID();

        when(authentication.getName()).thenReturn("admin@gmail.com");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        when(recordRepository.findById(id)).thenReturn(Optional.of(record));

        recordService.deleteRecord(id, authentication);

        verify(recordRepository).delete(record);
    }

    @Test
    void shouldThrowWhenDeleteNotAdmin() {
        User user = new User();
        user.setRole(Role.VIEWER);

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        Assertions.assertThrows(AppException.class, () -> {
            recordService.deleteRecord(UUID.randomUUID(), authentication);
        });
    }

    @Test
    void shouldFilterRecordsSuccessfully() {
        User user = new User();
        user.setEmail("test@gmail.com");

        Record record = new Record();
        record.setType(RecordType.INCOME);
        record.setCategory("Salary");
        record.setDate(LocalDate.now());
        record.setCreatedBy(user);

        when(authentication.getName()).thenReturn("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(recordRepository.findAll()).thenReturn(List.of(record));

        var result = recordService.getAllRecords(
                RecordType.INCOME,
                "Salary",
                LocalDate.now(),
                authentication
        );

        Assertions.assertEquals(1, result.size());
    }
}