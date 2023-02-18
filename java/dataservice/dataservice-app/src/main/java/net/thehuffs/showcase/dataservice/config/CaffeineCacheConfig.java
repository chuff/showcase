package net.thehuffs.showcase.dataservice.config;

import java.time.Duration;
import java.util.Arrays;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.github.benmanes.caffeine.cache.Caffeine;

@Profile("local")
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

  @Bean
  public CacheManager caffeineCacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(
        Arrays.asList(new CaffeineCache("foo", Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build())));
    return cacheManager;
  }


}
