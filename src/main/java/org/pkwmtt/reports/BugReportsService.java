package org.pkwmtt.reports;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.pkwmtt.reports.dto.BugReportDTO;
import org.pkwmtt.reports.mapper.BugReportsMapper;
import org.pkwmtt.reports.repositories.BugReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BugReportsService {
    
    private final BugReportRepository bugReportRepository;
    
    public List<BugReportDTO> getAllBugReports () {
        return bugReportRepository
          .findAll()
          .stream()
          .map(BugReportsMapper::toDto)
          .toList();
    }
    
    public void addBugReport (BugReportDTO bugReportDTO) {
        var bugReport = BugReportsMapper.toEntity(bugReportDTO);
        bugReportRepository.save(bugReport);
    }
    
    @Transactional
    public void removeBugReport (int id) {
        bugReportRepository.deleteById(id);
    }
}
