package org.gmr.app.minidrive.service;

import org.gmr.app.minidrive.dto.FileDownload;
import org.gmr.app.minidrive.dto.UploadResponse;
import org.gmr.app.minidrive.entity.FileMetadata;
import org.gmr.app.minidrive.entity.FileStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
public class FileService {

    private final FileStorageService storageService;
    private final FileMetadataService metadataService;

    public FileService(
            FileStorageService storageService,
            FileMetadataService metadataService
    ) {
        this.storageService = storageService;
        this.metadataService = metadataService;
    }

    public UploadResponse upload(
            MultipartFile file,
            String folder
    ) throws IOException {

        String normalizedFolder =
                storageService.normalizeFolderName(folder);

        String objectKey =
                storageService.upload(file, normalizedFolder);

        FileMetadata metadata = new FileMetadata(
                objectKey,
                file.getOriginalFilename(),
                normalizedFolder,
                file.getContentType(),
                file.getSize(),
                Instant.now(),
                FileStatus.ACTIVE
        );

        FileMetadata saved =
                metadataService.save(metadata);

        return new UploadResponse(
                saved.getId(),
                saved.getOriginalName(),
                saved.getFolder(),
                "Archivo subido correctamente"
        );
    }
    public FileDownload download(String id) {

        FileMetadata metadata = metadataService.findById(id);

        byte[] content = storageService.download(
                metadata.getObjectKey()
        );

        return new FileDownload(
                metadata.getOriginalName(),
                metadata.getContentType(),
                content
        );
    }

    public void delete(String id) {

        FileMetadata metadata = metadataService.findById(id);

        storageService.delete(metadata.getObjectKey());

        metadataService.delete(metadata);
    }
}






