package com.practice.fcm.common.base;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.*;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
