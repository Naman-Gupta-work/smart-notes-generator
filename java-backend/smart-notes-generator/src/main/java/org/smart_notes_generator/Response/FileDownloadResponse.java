package org.smart_notes_generator.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadResponse {

    private String status;
    private String message;
    private String jobId;
}
