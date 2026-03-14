package com.dataflow.budgetingservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Table(name = "currencies")
@Entity
@Immutable
@Data
public class Currency {
    @Id
    private String id;
    private String code;
    private String name;
}
