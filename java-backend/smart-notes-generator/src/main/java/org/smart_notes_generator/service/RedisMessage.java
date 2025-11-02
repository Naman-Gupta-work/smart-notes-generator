package org.smart_notes_generator.service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisMessage {
    private String filePath;
    private String action;
    private String jobId;
}
