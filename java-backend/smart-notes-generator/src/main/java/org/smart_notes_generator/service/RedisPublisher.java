package org.smart_notes_generator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {
    private final String channel= "java2python";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publish(String jobId,String videoPath,String action){
        RedisMessage redisMessage=RedisMessage.builder()
                        .jobId(jobId)
                        .filePath(videoPath)
                        .action(action)
                .build();
        redisTemplate.convertAndSend(channel, redisMessage);
        System.out.println("Message sent from Redis, path: "+videoPath+" action: "+action);
    }
}
