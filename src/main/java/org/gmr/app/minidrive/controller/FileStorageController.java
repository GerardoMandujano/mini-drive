package org.gmr.app.minidrive.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.gmr.app.minidrive.dto.*;
import org.gmr.app.minidrive.service.FileService;
import org.gmr.app.minidrive.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    private final FileService fileService;
    private final FileStorageService fileStorageService;
    public FileStorageController(
            FileService fileService,
            FileStorageService fileStorageService
    ) {
        this.fileService = fileService;
        this.fileStorageService = fileStorageService;
    }



    @GetMapping
    public ResponseEntity<FolderContentResponse> listFolder(
            @RequestParam(required = false, defaultValue = "")
            String folder) {

        return ResponseEntity.ok(
                fileStorageService.listFolder(folder)
        );
    }



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir archivos",
            description = "Almacena el archivo de s3 y su ,etadata en Mongo"
    )@ApiResponse(responseCode = "201",description = "Archivo subido correctamente")
    @ApiResponse(responseCode = "400",description = "Archivo o datos invalidos")
    @ApiResponse(
            responseCode = "500",description = "Error durante el almacenamiento"
    )
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(
                    required = false,
                    defaultValue = ""
            ) String folder
    ) throws IOException {

        UploadResponse response =
                fileService.upload(file, folder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable String id
    ) {

        FileDownload file = fileService.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        file.contentType()
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.fileName())
                                .build()
                                .toString()
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {

        fileService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
