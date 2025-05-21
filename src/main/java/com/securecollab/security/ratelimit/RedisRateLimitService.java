package com.securecollab.security.ratelimit;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimitService {

    private final LettuceBasedProxyManager<String> proxyManager;
    private final StatefulRedisConnection<String, byte[]> connection;

    private static final Bandwidth limit = Bandwidth.builder()
            .capacity(5)
            .refillGreedy(5, Duration.ofMinutes(1))
            .build();

    public RedisRateLimitService() {
        RedisClient client = RedisClient.create("redis://localhost:6379");

        RedisCodec<String, byte[]> codec = RedisCodec.of(new StringCodec(), new ByteArrayCodec());
        this.connection = client.connect(codec);

        this.proxyManager = LettuceBasedProxyManager.builderFor(connection).build();
    }

    public Bucket resolveBucket(String key) {
        return proxyManager.builder().build(key, () -> BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }

    @PreDestroy
    public void shutdown() {
        connection.close();
    }
}
