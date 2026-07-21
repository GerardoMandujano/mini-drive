package org.gmr.app.minidrive.dto;

public record FileDonwload(

        byte[] content,
        String contentType,
        String fileName

) {
}
