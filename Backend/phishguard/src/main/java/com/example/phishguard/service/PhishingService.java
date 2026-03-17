package com.example.phishguard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.phishguard.model.PhishingReport;
import com.example.phishguard.repository.PhishingReportRepository;

@Service
public class PhishingService {

    @Autowired
    private PhishingReportRepository repo;

    public PhishingReport report(PhishingReport report) {
        return repo.save(report);
    }
}