package umc.haruchi.config.redis;

import io.lettuce.core.dynamic.domain.Timeout;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save (String key, Object val, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

//    public void setValues(String key, String data) {
//        ValueOperations<String, Object> values = redisTemplate.opsForValue();
//        values.set(key, data);
//    }
//
//    public void setValues(String key, String data, Duration duration) {
//        ValueOperations<String, Object> values = redisTemplate.opsForValue();
//        values.set(key, data, duration);
//    }
//
//    public Object getValues(String key) {
//        ValueOperations<String, Object> values = redisTemplate.opsForValue();
//        return values.get(key);
//    }
//
//    public void deleteValues(String key) {
//        redisTemplate.delete(key);
//    }
}
