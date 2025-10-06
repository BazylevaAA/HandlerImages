package com.example.handler.service.handlerService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileHandlerService {
    void uploadFile(List<MultipartFile> files) throws Exception;
}
