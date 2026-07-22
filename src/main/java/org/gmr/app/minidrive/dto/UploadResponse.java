package org.gmr.app.minidrive.dto;


public record UploadResponse(
        String id,
        String fileName,
        String folder,
        String message
) {
}