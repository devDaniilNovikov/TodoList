package dn.tasktracker.configuration.cache;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "app.cache")
@Data
public class CacheConfig {

    private final List<String> cacheNames = new ArrayList<>();
    private final Map<String, CacheProperties> caches = new HashMap<>();
    private final CacheType cacheType = CacheType.REDIS;

    @Data
    public static class CacheProperties {
        private Duration ttl = Duration.ZERO;
    }

    public enum CacheType {
        REDIS
    }


}
