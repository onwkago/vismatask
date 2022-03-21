package com.visma.task.consumer.service;

import com.visma.task.consumer.exception.ThirdPartyServiceException;
import com.visma.task.consumer.model.Item;
import com.visma.task.consumer.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ProcessingService processingService;
    @Value("${thirdpartyservice.poll.backoff.duration:1000}")
    private long backoffDuration;

    public Mono<Item> createItem(String content) {
        log.debug("creating item");

        return processingService.callInit(content)
                .flatMap(uuid -> processingService.getStatus(uuid)
                        .repeatWhen(completed -> completed.delayElements(Duration.ofMillis(backoffDuration)))
                        .takeUntil(status -> {
                            log.debug("poll object {}, status: {}", uuid, status.getStatusType());
                            if (StatusType.FAILED.equals(status.getStatusType())) {
                                throw new ThirdPartyServiceException(String.format("Failed to create item %s", uuid));
                            }
                            return StatusType.OK.equals(status.getStatusType());
                        })
                        .last()
                        .flatMap(status -> Mono.just(Item.builder()
                                .status(status)
                                .content(content)
                                .build()))
                );
    }

}
