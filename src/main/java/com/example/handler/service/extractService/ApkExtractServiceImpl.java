package com.example.handler.service.extractService;

import com.example.handler.service.extractService.ApkExtractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApkExtractServiceImpl implements ApkExtractService {
    private static final Logger log = LoggerFactory.getLogger(ApkExtractServiceImpl.class);
    private static final String APK_PATH_SYSTEM = "C:/Users/n_baz/Desktop/handler/handler/handler/uploadIMG/system";
    private static final String APK_PATH_VENDOR = "C:/Users/n_baz/Desktop/handler/handler/handler/uploadIMG/vendor";
    private static final Path EXTRACT_CONTENT_FROM_APK = Paths.get("APK");

    private List<File> getApkFiles(){
        List<File> apkFiles = new ArrayList<>();
        log.info("Поиск APK файлов в директории system");
        scanDirectory(new File(APK_PATH_SYSTEM), apkFiles);
        log.info("Поиск APK файлов в директории vendor");
        scanDirectory(new File(APK_PATH_VENDOR), apkFiles);
        return apkFiles;
    }

    private static void scanDirectory(File dir, List<File> apkFiles) {
        Integer countFiles = 0;
        File[] entries = dir.listFiles();
        if (entries == null) return;
        for (File entry : entries) {
            if (entry.isDirectory()) {
                scanDirectory(entry, apkFiles);
            } else if (entry.getName().toLowerCase().endsWith(".apk")) {
                countFiles++;
                System.out.println("APK - " + entry.getName() + " from path: " + entry.getAbsolutePath());
                apkFiles.add(entry);
            }
        }
        log.info("Найдено apk : " + countFiles);
        countFiles = 0;
    }

    @Override
    public void extractApkFile () throws Exception{
        Files.createDirectory(EXTRACT_CONTENT_FROM_APK);
        List<File> apkFiles = getApkFiles();
        for (File apkFile : apkFiles) {
            try{
                extractApk(apkFile);
                log.info("Успех!!!");
            }catch (Exception e){
                log.error("Ошибка!!!");
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static void extractApk(File file) throws Exception{
        Path outDir = EXTRACT_CONTENT_FROM_APK.resolve(file.getName());
        String apktoolPath = "C:/Users/n_baz/Desktop/handler/handler/handler/apktool_2.12.1.jar";

        List<String> cmd = Arrays.asList("java", "-jar", apktoolPath, "-d", "-f" , "-o" ,outDir.toAbsolutePath().toString(), file.getAbsolutePath());
        log.info("Запуск apktool: {}", String.join(" ", cmd));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                log.info("[apktool: ] " + line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0){
            throw new RuntimeException("apktool завершил работу с кодом ошибки: " + exitCode);
        }
        else {
            log.info("Разбор APK успешно завершился");
        }

    }
}
