package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Controller
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private ObjectStorageService objectStorageService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String path = objectStorageService.uploadFile(file, "uploads");
            String publicUrl = objectStorageService.getPublicUrl(path);
            return Result.success(publicUrl);
        } catch (Exception e) {
            return Result.error("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload/multiple")
    public Result<String[]> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            String[] urls = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                String path = objectStorageService.uploadFile(files[i], "uploads");
                urls[i] = objectStorageService.getPublicUrl(path);
            }
            return Result.success(urls);
        } catch (Exception e) {
            return Result.error("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam String path) {
        try {
            objectStorageService.deleteFile(path);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Delete failed: " + e.getMessage());
        }
    }
}
