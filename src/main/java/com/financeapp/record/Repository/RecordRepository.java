package com.financeapp.record.Repository;

import com.financeapp.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecordRepository extends JpaRepository<Record, UUID> {
}
