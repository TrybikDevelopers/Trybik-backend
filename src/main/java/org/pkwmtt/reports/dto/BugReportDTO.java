package org.pkwmtt.reports.dto;


import lombok.Getter;

import java.util.Date;

@Getter
public class BugReportDTO extends NewBugReportDTO {
    
    int reportId;

    public BugReportDTO (int reportId, String userGroups, String description, Date issuedAt) {
        super(userGroups, description, issuedAt);
        this.reportId = reportId;
    }
}
