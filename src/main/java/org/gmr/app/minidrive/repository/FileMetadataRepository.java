package org.gmr.app.minidrive.repository;

import org.gmr.app.minidrive.entity.FileMetadata;
import org.gmr.app.minidrive.entity.FileStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataRepository
        extends MongoRepository<FileMetadata, String> {

    List<FileMetadata> findByFolderAndStatus(
            String folder,
            FileStatus status
    );
}