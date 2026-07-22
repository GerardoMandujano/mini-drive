package org.gmr.app.minidrive.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;


@Getter
@Setter
@Document(collection = "files")
public class FileMetadata {

        @Id
        private String id;

        @Indexed(unique = true)
        private String objectKey;

        private String originalName;
        private String folder;
        private String contentType;
        private Long size;
        private Instant uploadedAt;
        private FileStatus status;

        public FileMetadata() {
        }

        public FileMetadata(
                String objectKey,
                String originalName,
                String folder,
                String contentType,
                Long size,
                Instant uploadedAt,
                FileStatus status
        ) {
            this.objectKey = objectKey;
            this.originalName = originalName;
            this.folder = folder;
            this.contentType = contentType;
            this.size = size;
            this.uploadedAt = uploadedAt;
            this.status = status;
        }
}
