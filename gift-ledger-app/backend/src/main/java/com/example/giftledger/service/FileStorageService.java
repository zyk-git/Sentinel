package com.example.giftledger.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // 上传图片到 static/uploads，返回可直接访问的相对路径
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int index = originalName.lastIndexOf('.');
        if (index > -1) {
            ext = originalName.substring(index);
        }

        String filename = UUID.randomUUID() + ext;
        Path basePath = Paths.get("src/main/resources/static", uploadDir);
        Files.createDirectories(basePath);
        Path target = basePath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/" + uploadDir + "/" + filename;
    }
}
