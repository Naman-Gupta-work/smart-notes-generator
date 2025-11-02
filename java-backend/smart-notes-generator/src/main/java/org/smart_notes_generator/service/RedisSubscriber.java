package org.smart_notes_generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisSubscriber implements MessageListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonMessage = new String(message.getBody());

        try {
            PythonResponse response = objectMapper.readValue(jsonMessage, PythonResponse.class);
            if ("SUCCESS".equals(response.getStatus())) {
                String key = "result:" + response.getJobId();
                redisTemplate.opsForValue().set(key, response.getData(), 3600, TimeUnit.SECONDS);
            } else {
                System.out.println(" Received ERROR from Python for job: " +
                        response.getJobId() + ", Error: " + response.getData());
            }

        } catch (Exception e) {
            System.err.println("Could not parse message from Python: " + jsonMessage);
        }
    }

    // A simple inner class to map the incoming JSON from Python
    @Data
    private static class PythonResponse {
        private String jobId;
        private String status;
        private String type;
        private String data;
    }
}