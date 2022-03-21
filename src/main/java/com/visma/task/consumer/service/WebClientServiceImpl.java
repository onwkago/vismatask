package com.visma.task.consumer.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientServiceImpl implements WebClientService {

    private final WebClient webClient;

    @Override
    public <T, U> Mono<T> postJson(URI uri, U requestBody, Class<T> responseType) {
        log.debug("Reactive post json to url:{}", uri.getPath());
        return webClient.post()
                .uri(uri)
                .headers(consumer -> initHttpHeaders(MediaType.APPLICATION_JSON))
                .body(Mono.just(requestBody), requestBody.getClass())
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .bodyToMono(responseType)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(3))
                        .jitter(0.3)
                        .filter(throwable -> throwable instanceof ConnectException)
                );
    }

    @Override
    public <T> Mono<T> get(URI uri, Class<T> responseType) {
        log.debug("Reactive get from url:{}", uri.getPath());
        return webClient.get()
                .uri(uri)
                .headers( consumer -> initHttpHeaders())
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .bodyToMono(responseType)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(5))
                        .jitter(0.3)
                        .filter(throwable -> throwable instanceof ConnectException)
                );
    }

    private HttpHeaders initHttpHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        Optional.ofNullable(mediaType).ifPresent(headers::setContentType);
        return headers;
    }

    private HttpHeaders initHttpHeaders() {
        return initHttpHeaders(null);
    }
}
