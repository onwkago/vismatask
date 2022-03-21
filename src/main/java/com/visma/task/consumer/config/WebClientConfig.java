package com.visma.task.consumer.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${thirdpartyservice.client.connect.timeout}")
    private Integer connectTimeout;
    @Value("${thirdpartyservice.client.read.timeout}")
    private Integer readTimeout;
    @Value("${thirdpartyservice.client.max.connections}")
    private Integer maxConnections;
    @Value("${thirdpartyservice.client.connection.acquire.timeout}")
    private Integer acquireTimeout;
    @Value("${thirdpartyservice.client.connection.acquire.max.count}")
    private Integer maxCount;

    @Bean
    public WebClient getWebClient() {
        log.debug("creating web client, connect timeout: {}, read timeout: {}", connectTimeout,readTimeout);
        final var provider = ConnectionProvider.builder("fixed-connections")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(acquireTimeout))
                .pendingAcquireMaxCount(maxCount)
                .build();
        final var httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected( connection -> connection
                        .addHandler(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
