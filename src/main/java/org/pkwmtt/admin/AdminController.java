package org.pkwmtt.admin;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.exams.enums.Role;
import org.pkwmtt.reports.BugReportsService;
import org.pkwmtt.reports.dto.BugReportDTO;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ApiKeyService service;
    private final AdminService adminService;
    private final BugReportsService bugReportsService;
    
    
    @PostMapping("/api/keys/generate")
    public String generateApiKey (@RequestParam(name = "d") String description,
                                  @RequestParam(name = "r") Role role) {
        return service.generateApiKey(description, role);
    }
    
    @GetMapping("/api/keys")
    public Map<String, String> getMapOfPublicApiKeys () {
        return service.getMapOfPublicApiKeys();
    }
    
    @PostMapping("/add-moderator")
    public ResponseEntity<String> addModerator () {
        return ResponseEntity.ok(adminService.addModerator());
    }
    
    @GetMapping("/bugs/reports")
    public ResponseEntity<List<BugReportDTO>> getBugReports () {
        return ResponseEntity.ok(bugReportsService.getAllBugReports());
    }
    
    @DeleteMapping("/bugs/reports")
    public ResponseEntity<Void> deleteBugReport (@RequestParam(name = "id") int id) {
        bugReportsService.removeBugReport(id);
        return ResponseEntity.ok().build();
    }
    
}
