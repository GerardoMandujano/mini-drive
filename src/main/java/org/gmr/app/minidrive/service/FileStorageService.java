package org.gmr.app.minidrive.service;

import org.gmr.app.minidrive.dto.FileDonwload;
import org.gmr.app.minidrive.dto.FileResponse;
import org.gmr.app.minidrive.dto.FolderContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    private final S3Client s3Client;
    private final String bucket;

    public FileStorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket) {

        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file,String folder) throws IOException {

        String fileName = file.getOriginalFilename();

        String objectKey = folder.isBlank()
                ? fileName: folder + "/" + fileName;


        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );

        return objectKey;
    }

    public List<FileResponse> listFiles(String folder) {

        String prefix = normalizeFolder(folder);

        return s3Client.listObjectsV2(builder -> builder
                        .bucket(bucket)
                        .prefix(prefix)
                )
                .contents()
                .stream()
                .map(object -> new FileResponse(
                        object.key(),
                        object.size(),
                        null,
                        object.lastModified()
                ))
                .toList();
    }

    public FileDonwload download(String key){
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key).build();
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
        String contentType = response.response().contentType();
        String fileName = extractOriginalFileName(key);
        return  new FileDonwload(
                response.asByteArray(),
                contentType,
                fileName
        );
    }

    public FolderContentResponse listFolder(String folder) {

        String prefix = normalizeFolder(folder);

        ListObjectsV2Response response =
                s3Client.listObjectsV2(builder -> builder
                        .bucket(bucket)
                        .prefix(prefix)
                        .delimiter("/")
                );

        List<String> folders = response.commonPrefixes()
                .stream()
                .map(commonPrefix ->
                        removeCurrentPrefix(
                                commonPrefix.prefix(),
                                prefix
                        )
                )
                .toList();

        List<FileResponse> files = response.contents()
                .stream()
                .filter(object -> !object.key().equals(prefix))
                .map(object -> new FileResponse(
                        object.key(),
                        object.size(),
                        null,
                        object.lastModified()
                ))
                .toList();

        return new FolderContentResponse(
                prefix,
                folders,
                files
        );
    }

    private String extractOriginalFileName(String key) {

        int separatorPosition = key.indexOf("-");

        if (separatorPosition == -1) {
            return key;
        }

        return key.substring(separatorPosition + 1);
    }

    private String normalizeFolder(String folder) {

        if (folder == null || folder.isBlank()) {
            return "";
        }

        String normalized = folder.trim();

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(
                    0,
                    normalized.length() - 1
            );
        }

        return normalized + "/";
    }

    private String removeCurrentPrefix(
            String completeFolder,
            String currentPrefix) {

        if (currentPrefix.isBlank()) {
            return completeFolder;
        }

        return completeFolder.substring(
                currentPrefix.length()
        );
    }
}
