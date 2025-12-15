package org.pkwmtt.reports.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "bug_reports")
@Getter
@NoArgsConstructor
public class BugReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    int reportId;
    
    @Column(name = "user_groups", columnDefinition = "VARCHAR(255)")
    
    String userGroups;
    @Column(name = "description", columnDefinition = "VARCHAR(1000)")
    String description;
    
    @Column(name = "issued_at", columnDefinition = "TIMESTAMP")
    Date issuedAt;
    
    public BugReport (String userGroups, String description, Date issuedAt) {
        this.userGroups = userGroups;
        this.description = description;
        this.issuedAt = issuedAt;
    }
}
