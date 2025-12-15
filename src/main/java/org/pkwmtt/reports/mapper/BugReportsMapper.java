package org.pkwmtt.reports.mapper;

import org.pkwmtt.reports.dto.BugReportDTO;
import org.pkwmtt.reports.dto.NewBugReportDTO;
import org.pkwmtt.reports.entities.BugReport;

public class BugReportsMapper {
    private BugReportsMapper () {
    }
    
    
    public static BugReportDTO toDto (BugReport src) {
        if (src == null) {
            return null;
        }
        
        return new BugReportDTO(
          src.getReportId(),
          src.getUserGroups(),
          src.getDescription(),
          src.getIssuedAt()
        );
    }
    
    public static BugReport toEntity (NewBugReportDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new BugReport(
          dto.getUserGroups(),
          dto.getDescription(),
          dto.getIssuedAt()
        );
    }
}
