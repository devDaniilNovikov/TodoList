package dn.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RedisBatchService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void batchInsertWithPipeline(String keyPrefix, List<Object> values) {
         redisTemplate.executePipelined((RedisCallback<?>) (redisConnection) -> {
            for (int i = 0; i < values.size(); i++) {
                String key = keyPrefix + ":" + i;
                redisConnection.stringCommands().set(key.getBytes(),
                                values.get(i).toString()
                                        .getBytes());
            }
            return null;
        });
    }








}
