package org.smart_notes_generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/v1/results")
@CrossOrigin(origins = "*")
public class ResultController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping(value = "/{jobId}/audio", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getAudioResult(@PathVariable String jobId) {

        String key = "result:" + jobId;

        String base64Audio = (String) redisTemplate.opsForValue().get(key);

        if (base64Audio == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
        }


        byte[] audioBytes = Base64.getDecoder().decode(base64Audio);


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audioBytes);
    }

}
