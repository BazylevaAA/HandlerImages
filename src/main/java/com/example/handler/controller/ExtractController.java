package com.example.handler.controller;

import com.example.handler.service.extractService.ApkExtractService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class ExtractController {
    private final ApkExtractService service;
    private static final Logger log = LoggerFactory.getLogger(ExtractController.class);

    @GetMapping("/extract")
    public ResponseEntity<String> extract() throws Exception {
        service.extractApkFile();
        return ResponseEntity.ok("Успех !!!");
    }
}
