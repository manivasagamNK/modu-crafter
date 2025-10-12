package com.moducrafter.appMod.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MAPPING")
@Setter
@Getter
public class Mapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mappingId;
    @OneToOne
    @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID")
    private Employee empId;
    @ManyToOne
    @JoinColumn(name = "SUPERVISOR_ID", referencedColumnName = "EMP_ID")
    private Employee supervisorId;
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @Column(name = "BILLABLE")
    private Boolean Billable;
    @Column(name = "UPDATED_TS")
    private LocalDateTime updatedTs;
}
