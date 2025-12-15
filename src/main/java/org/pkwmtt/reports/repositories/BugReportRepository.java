package org.pkwmtt.reports.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.pkwmtt.reports.entities.BugReport;

import java.util.List;

public interface BugReportRepository extends JpaRepository<BugReport, Integer> {
    @Override
    @NonNull
    List<BugReport> findAll ();
}
