package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.FileUploadResponse;
import com.aimarketplace.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private ObjectStorageService storageService;

    // 允许的文件类型
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("md", "zip"));
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @PostMapping("/upload")
    public Result<FileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        try {
            // 验证文件大小
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("文件大小不能超过 50MB");
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return Result.error("只支持 .md 和 .zip 文件");
            }

            // 生成唯一文件名
            String key = generateKey(file.getOriginalFilename(), request);

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                storageService.uploadFile(key, inputStream, file.getSize(), file.getContentType());
            }

            // 返回结果
            FileUploadResponse response = new FileUploadResponse();
            response.setKey(key);
            response.setUrl(storageService.getPresignedUrl(key, 3600));
            response.setSize(file.getSize());
            response.setFileName(originalFilename);

            return Result.success(response);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{key:.+}")
    public Result<String> getDownloadUrl(@PathVariable String key) {
        try {
            if (!storageService.fileExists(key)) {
                return Result.error("文件不存在");
            }
            String url = storageService.getPresignedUrl(key, 3600);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("获取下载链接失败");
        }
    }

    private String generateKey(String filename, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String extension = filename.substring(filename.lastIndexOf("."));
        return "uploads/" + userId + "/" + UUID.randomUUID() + extension;
    }
}
