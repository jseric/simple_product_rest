package com.jseric.simple_product_rest.model.base;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_timestamp", nullable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    private LocalDateTime updatedTimestamp;

    @Column(name = "deleted")
    private LocalDateTime deleted;

    public BaseEntity(Long id) {
        this.id = id;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdTimestamp = LocalDateTime.now();
        this.updatedTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedTimestamp = LocalDateTime.now();
    }
}
