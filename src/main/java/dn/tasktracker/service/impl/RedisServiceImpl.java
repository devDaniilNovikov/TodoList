package dn.tasktracker.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration ttl;

    @Override
    @Loggable
    public WeakReference<String> writeInRedis(Object element,Long keyOfElement) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String key = String.valueOf(keyOfElement);
            String value = objectMapper.writeValueAsString(element);
            List<Object> txResult = createInTransaction(key,value);
            var hashSetValue = Objects.requireNonNull(redisTemplate.opsForSet().members(key));
            HashSet<Object> members = new HashSet<>(hashSetValue);
            var result = Objects.requireNonNull(members).toString();
            log.info("Successful writing in redis, key: {}, value: {}",key,value);
            log.info("Transaction result is: {}",txResult.toString());
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
        }); //TODO: написать реализацию
    }


    public List<Object> createInTransaction(String key,String value){
        return redisTemplate.execute(new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(RedisConnection connection){
                try {
                    connection.multi();
                    connection.setCommands().sAdd(key.getBytes(), value.getBytes());
                    connection.expire(key.getBytes(), ttl.getSeconds());
                    log.info("Key in transaction: {}, Value: {}, Ttl: {}",key,value,ttl.getSeconds());
                    return connection.exec();
                }catch (IllegalTransactionStateException | DataAccessException ex){
                    log.error("Bad transaction execution: {}",ex.getLocalizedMessage());
                    throw new RuntimeException();
                }
            };
        });
    }
}
