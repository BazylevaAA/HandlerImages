package com.example.handler.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    private final Path uploadDir = Paths.get("uploadIMG");
    private static final Logger log = LoggerFactory.getLogger(FileHandlerServiceImpl.class);

    @PostConstruct
    void init() throws Exception {
        log.info("Создание проектных директорий: system, vendor, system_ext");
        Files.createDirectories(uploadDir);
        Files.createDirectories(uploadDir.resolve("system"));
        Files.createDirectories(uploadDir.resolve("vendor"));
        Files.createDirectories(uploadDir.resolve("system_ext"));
        log.info("Директории созданы!");
    }

    @Override
    public void uploadFile(List<MultipartFile> files) throws Exception {
        log.info("Начало загрузки файлов");

        for (MultipartFile file : files) {
            String original = file.getOriginalFilename();
            if (original == null || original.isBlank()) {
                throw new Exception("Пустое имя файла");
            }
            String safeName = Paths.get(original).getFileName().toString();
            Path target = uploadDir.resolve(safeName);

            log.info("Сохранение файла: {}", safeName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Файл {} успешно сохранен", safeName);
        }

        executeScript();
    }

    private void executeScript() throws Exception {
        Path base = uploadDir.toAbsolutePath();
        String scriptPath = "C:/Users/n_baz/Desktop/handler/handler/new_script.ps1";

        ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe",
                "-ExecutionPolicy", "Bypass",
                "-File",
                scriptPath,
                base.toString()
        );

        pb.directory(new File("C:/Users/n_baz/Desktop/handler/handler"));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Чтение вывода скрипта
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("PowerShell: {}", line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new Exception("Ошибка распаковки. Код выхода: " + exitCode);
        }
    }

}