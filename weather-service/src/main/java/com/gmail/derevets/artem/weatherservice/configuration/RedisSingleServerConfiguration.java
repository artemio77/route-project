package com.gmail.derevets.artem.weatherservice.configuration;


import com.gmail.derevets.artem.weatherservice.cache.RedisMessageCache;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(prefix = "redisson", name = "mode", havingValue = "single")
public class RedisSingleServerConfiguration {

    @Bean
    public RedisMessageCache alertCache(RedissonClient redissonClient) {
        return new RedisMessageCache(redissonClient.getMapCache("ALERT_SCOPE"));
    }

    @Bean
    @DependsOn("singleServerConfig")
    public RedissonClient redissonClient(Config config) {
        return Redisson.create(config);
    }

    @Bean
    Config config() {
        return new Config();
    }

    @Bean
    @ConfigurationProperties(prefix = "redisson")
    SingleServerConfig singleServerConfig(Config config) {
        return config.useSingleServer();
    }
}