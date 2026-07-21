package org.gmr.app.minidrive.dto;

import java.time.Instant;

public record FileResponse (
        String key,
        Long size,
        String contentType,
        Instant lastMoment
){


}
