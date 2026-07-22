package org.gmr.app.minidrive.service;

import org.gmr.app.minidrive.entity.FileMetadata;
import org.gmr.app.minidrive.entity.FileStatus;
import org.gmr.app.minidrive.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileMetadataService {

    private final FileMetadataRepository repository;

    public FileMetadataService(
            FileMetadataRepository repository
    ) {
        this.repository = repository;
    }

    public FileMetadata save(FileMetadata metadata) {
        return repository.save(metadata);
    }

    public FileMetadata findById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Archivo no encontrado: " + id
                        )
                );
    }



    public List<FileMetadata> findActiveByFolder(
            String folder
    ) {
        return repository.findByFolderAndStatus(
                folder,
                FileStatus.ACTIVE
        );
    }
    public void delete(FileMetadata metadata) {
        repository.delete(metadata);
    }
}