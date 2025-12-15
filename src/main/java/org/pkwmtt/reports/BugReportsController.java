package org.pkwmtt.reports;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.reports.dto.BugReportDTO;
import org.pkwmtt.reports.dto.NewBugReportDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${apiPrefix}/bug-reports")
@RequiredArgsConstructor
@RestController
public class BugReportsController {
    private final BugReportsService service;
    
    @PostMapping("/report")
    public ResponseEntity<Void> reportBug (@RequestBody NewBugReportDTO bugReportDTO) {
        
        service.addBugReport(new BugReportDTO(
          0,
          bugReportDTO.getUserGroups(),
          bugReportDTO.getDescription(),
          bugReportDTO.getIssuedAt()
        ));
        
        return ResponseEntity.ok().build();
    }
}
