package com.example.handler.controller;

import com.example.handler.service.handlerService.FileHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class HandlerController {
    private final FileHandlerService service;

    @PostMapping("/load")
    public ResponseEntity<String> upload(@RequestParam("file") List<MultipartFile> files)throws Exception{
        service.uploadFile(files);
        return ResponseEntity.ok("ОК!!!");
    }
}
