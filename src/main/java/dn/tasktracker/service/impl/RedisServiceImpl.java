package dn.tasktracker.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration ttl;

    @Override
    public WeakReference<String> writeInRedis(Object element,Long keyOfElement) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String key = String.valueOf(keyOfElement);
            String value = objectMapper.writeValueAsString(element);
            List<Object> txResult = redisTemplate.execute(new RedisCallback<List<Object>>() {
                @Override
                public List<Object> doInRedis(@NotNull RedisConnection connection) throws DataAccessException {
                    connection.multi();
                    connection.setCommands().sAdd(key.getBytes(), value.getBytes());
                    connection.expire(key.getBytes(), ttl.getSeconds());
                    return connection.exec();
                }
            });
            log.info("Transaction result is: {}",txResult);
            var result = Objects.requireNonNull(redisTemplate.opsForSet().members(key)).toString();
            log.info("Successful writing in redis, key: {}, value: {}",key,value);
            return new WeakReference<>(result);
        }catch (JsonProcessingException e){
            log.error("Can't write in redis: {}",e.getLocalizedMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public void updateInBatch() {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.commands().flushAll();
                return null;
            };
        });
    } //TODO:
}
