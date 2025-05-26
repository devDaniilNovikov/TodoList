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
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.*;
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration ttl;

    @Override
    @Loggable
    public SoftReference<String> writeInRedis(Object element, Long keyOfElement) {
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
            return new SoftReference<>(result);
        }catch (JsonProcessingException e){
            log.error("Can't write in redis: {}",e.getLocalizedMessage());
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional
    @Loggable
    public void updateInBatch() {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.commands().flushAll();
                return null;
            };
        }); //TODO: написать реализацию
    }

    @Override
    @Transactional
    @Loggable
    public void deleteAllInBatchByKeys(List<Byte> keyList) {
        if (keyList.isEmpty()){
            throw new IllegalArgumentException("KeyList can't be empty!");
        }
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(@NotNull RedisConnection connection) throws DataAccessException {
                try {
                    connection.openPipeline();
                    byte[] keyByteArray = new byte[keyList.size()];
                    for (int i = 0; i < keyList.size(); i++) {
                        keyByteArray[i] = keyList.get(i);
                    }
                    connection.keyCommands().del(keyByteArray);
                    log.info("Keys deleted in batch: {}", Arrays.toString(keyByteArray));
                    return connection.closePipeline();
                }catch (DataAccessException | IllegalArgumentException e){
                    log.error("Can't make batch operation. Exception: {}",e.getLocalizedMessage());
                    throw new RuntimeException();
                }
            }
        });
    }


    public List<Object> createInTransaction(String key,String value){
        return redisTemplate.execute(new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(@NotNull RedisConnection connection){
                try {
                    byte[] keyByteArray = key.getBytes();
                    byte[] valueByteArray = value.getBytes();
                    connection.multi();
                    connection.setCommands().sAdd(keyByteArray, valueByteArray);
                    connection.expire(keyByteArray, ttl.getSeconds());
                    log.info("Key in transaction: {}, Value: {}, Ttl: {}",key,value,ttl.getSeconds());
                    return connection.exec();
                }catch (IllegalTransactionStateException | DataAccessException ex){
                    log.error("Bad transaction execution: {}",ex.getLocalizedMessage());
                    throw new RuntimeException();
                }
                finally {
                    connection.closePipeline();
                }
            };
        });
    }
}
