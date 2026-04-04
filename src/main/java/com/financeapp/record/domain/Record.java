package com.financeapp.record.domain;

import com.financeapp.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    @Id
    @GeneratedValue
    private UUID id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String category;

    private LocalDate date;

    private String note;


    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;


    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
