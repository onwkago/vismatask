package com.visma.task.consumer.service;

import reactor.core.publisher.Mono;

import java.net.URI;

public interface WebClientService {

    <T, U> Mono<T> postJson(URI uri, U requestBody, Class<T> responseType);

    <T> Mono<T> get(URI uri, Class<T> responseType);

}
