package org.example.concurrency_example.domain.ticket.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(Long key) {
        Boolean result = redisTemplate
                .opsForValue()
                .setIfAbsent(key.toString(), "lock", Duration.ofSeconds(5));
        System.out.println("Lock acquired: " + result);
        return result;
    }

    public Boolean unlock(Long key) {
        Boolean result = redisTemplate.delete(key.toString());
        System.out.println("Lock released: " + result);
        return result;
    }
}
