package org.gmr.app.minidrive.dto;

public record FileDownload(
        String fileName,
        String contentType,
        byte[] content
) {
}