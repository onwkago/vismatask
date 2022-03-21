package com.visma.task.consumer.service;

import com.visma.task.consumer.model.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ProcessingService {

    @Value("${thirdpartyservice.uri.scheme}")
    private String scheme;
    @Value("${thirdpartyservice.uri.host}")
    private String host;
    @Value("${thirdpartyservice.uri.port}")
    private Integer port;
    @Value("${thirdpartyservice.uri.contextpath}")
    private String contextPath;

    private static final String ENDPOINT_INIT = "/init";
    private static final String ENDPOINT_CHECK_STATUS = "/checkStatus/{uuid}";

    private final WebClientService webClientService;

    public ProcessingService(WebClientService webClientService) {
        this.webClientService = webClientService;
    }

    public Mono<String> callInit(String content) {
        Mono<String> response = webClientService.postJson(buildUri(ENDPOINT_INIT), content, String.class);
        return response;
    }

    public Mono<Status> getStatus(String uuid) {
        Mono<Status> response = webClientService.get(buildUri(ENDPOINT_CHECK_STATUS, uuid), Status.class);
        return response;
    }

    private URI buildUri(String path, Object... uriVariables) {
        UriBuilderFactory factory = new DefaultUriBuilderFactory();
        return factory.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(contextPath)
                .path(path)
                .build(uriVariables);
    }

}
