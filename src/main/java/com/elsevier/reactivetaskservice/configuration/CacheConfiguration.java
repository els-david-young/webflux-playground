package com.elsevier.reactivetaskservice.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfiguration {

  @Bean
  public Config hazelcastConfig() {
    return new Config()
        .setInstanceName("hazelcast-instance")
        .addMapConfig(
            new MapConfig()
                .setName("task-updates")
                .setTimeToLiveSeconds(30));
  }
}
