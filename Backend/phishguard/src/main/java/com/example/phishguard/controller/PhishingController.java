package com.example.phishguard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.phishguard.model.PhishingReport;
import com.example.phishguard.service.PhishingService;

@RestController
@CrossOrigin
@RequestMapping("/phishing")
public class PhishingController {

    @Autowired
    private PhishingService service;

    @PostMapping
    public PhishingReport report(@RequestBody PhishingReport report) {
        return service.report(report);
    }
}