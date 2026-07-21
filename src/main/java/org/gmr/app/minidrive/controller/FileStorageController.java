package org.gmr.app.minidrive.controller;


import org.gmr.app.minidrive.dto.FileDonwload;
import org.gmr.app.minidrive.dto.FileResponse;
import org.gmr.app.minidrive.dto.FolderContentResponse;
import org.gmr.app.minidrive.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private  final S3Client s3Client;
    private  String bucket;

    public FileStorageController(S3Client s3Client,
                                 @Value("${aws.s3.bucket}") String bucket,FileStorageService fileStorageService) {
        this.s3Client = s3Client;
        this.bucket=bucket;
        this.fileStorageService=fileStorageService;
    }

    @GetMapping("/list")
    public List<String> listFiles() {
        return s3Client.listObjectsV2(builder -> builder.bucket(bucket))
                .contents()
                .stream()
                .map(S3Object::key)
                .toList();
    }
    @GetMapping
    public ResponseEntity<FolderContentResponse> listFolder(
            @RequestParam(required = false, defaultValue = "")
            String folder) {

        return ResponseEntity.ok(
                fileStorageService.listFolder(folder)
        );
    }



    @PostMapping
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "") String folder)
            throws IOException {

        String objectKey =
                fileStorageService.upload(file, folder);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Archivo subido correctamente",
                        "key", objectKey
                ));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String key) {

        FileDonwload file = fileStorageService.download(key);

        return ResponseEntity.ok()
                .contentType(resolveMediaType(file.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.fileName() + "\""
                )
                .body(file.content());
    }

    private MediaType resolveMediaType(String contentType) {

        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
