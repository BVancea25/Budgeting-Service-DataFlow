package com.dataflow.budgetingservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "categories")
@Immutable
@Data
public class Category {

    @Id
    private String id;

    private String name;
}
