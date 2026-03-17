package com.example.phishguard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.phishguard.model.PhishingReport;

public interface PhishingReportRepository extends JpaRepository<PhishingReport, Long> {
}