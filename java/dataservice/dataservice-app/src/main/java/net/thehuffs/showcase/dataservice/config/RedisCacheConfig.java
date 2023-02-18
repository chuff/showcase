package net.thehuffs.showcase.dataservice.config;

import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.google.protobuf.InvalidProtocolBufferException;
import net.thehuffs.showcase.dataservice.proto.Foo;

@Profile("!local")
@Configuration
@EnableCaching
public class RedisCacheConfig {

  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration fooCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new RedisSerializer<Foo>() {

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

            }));



    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
        .withCacheConfiguration("foo", fooCacheConfiguration).build();
  }

}
