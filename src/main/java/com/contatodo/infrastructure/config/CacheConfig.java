package com.contatodo.infrastructure.config;

import com.contatodo.shared.constants.DealConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for application modules.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates the cache manager used by cacheable service operations.
     *
     * @return Cache manager.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                DealConstants.CACHE_DEALS_BY_COMPANY,
                DealConstants.CACHE_ACTIVE_NOT_DELETED_DEALS_BY_COMPANY,
                DealConstants.CACHE_DEAL_OFFER_BY_PRODUCT
        );
    }
}
