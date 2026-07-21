package org.gmr.app.minidrive.dto;

import java.util.List;

public record FolderContentResponse(
        String currentFolder,
        List<String> folders,
        List<FileResponse> files
) {
}