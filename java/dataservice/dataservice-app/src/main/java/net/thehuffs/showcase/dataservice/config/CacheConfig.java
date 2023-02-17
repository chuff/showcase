package net.thehuffs.showcase.dataservice.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.protobuf.InvalidProtocolBufferException;
import net.thehuffs.showcase.dataservice.proto.Foo;

@Configuration
@EnableCaching
public class CacheConfig {

  @Profile("!local")
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    // @formatter:off
    return (builder) -> builder
        .withCacheConfiguration("foo",
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .disableCachingNullValues()
            .serializeValuesWith(SerializationPair.fromSerializer(new RedisSerializer<Foo>() {

              @Override
              public Foo deserialize(byte[] b) throws SerializationException {
                try {
                  return Foo.parseFrom(b);
                } catch (InvalidProtocolBufferException e) {
                  throw new SerializationException(e.toString(), e);
                }
              }

              @Override
              public byte[] serialize(Foo foo) throws SerializationException {
                return foo.toByteArray();
              }

            })));
    // @formatter:on
  }

  @Bean
  @Profile("local")
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    // @formatter:off
    cacheManager.setCaches(Arrays.asList(
        new CaffeineCache("foo", Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build())
    ));
    // @formatter:on
    return cacheManager;
  }

}
