package com.visma.task.consumer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.visma.task.consumer.exception.ThirdPartyServiceException;
import com.visma.task.consumer.model.Item;
import com.visma.task.consumer.model.Status;
import com.visma.task.consumer.model.StatusType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ProcessingService processingService;

    @Test
    void createItemTest_shouldReturnItemWithStatusOk() {
        var content = RandomStringUtils.randomAlphabetic(10);
        var uuid = RandomStringUtils.randomAlphabetic(10);
        var uuidMono = Mono.just(uuid);
        var status1 = new Status();
        status1.setStatusType(StatusType.OK);
        status1.setUuid(uuid);
        var testResult = Item.builder()
                .status(status1)
                .content(content)
                .build();

        when(processingService.callInit(any())).thenReturn(uuidMono);
        when(processingService.getStatus(any())).thenReturn(Mono.just(status1));

        StepVerifier.create(itemService.createItem(content))
                .expectNext(testResult)
                .verifyComplete();

        verify(processingService,times(1)).getStatus(any());
    }

    @Test
    void createItemTest_shouldReturnExceptionWhenStatusFailed() {
        var content = RandomStringUtils.randomAlphabetic(10);
        var uuid = RandomStringUtils.randomAlphabetic(10);
        var uuidMono = Mono.just(uuid);
        var status1 = new Status();
        status1.setStatusType(StatusType.FAILED);
        status1.setUuid(uuid);

        when(processingService.callInit(any())).thenReturn(uuidMono);
        when(processingService.getStatus(any())).thenReturn(Mono.just(status1));
        StepVerifier.create(itemService.createItem(content))
                .verifyError(ThirdPartyServiceException.class);

        verify(processingService,times(1)).getStatus(any());
    }

    @Test
    void createItemTest_shouldWaitUntilStatusOkThenReturnItem(){
        var content = RandomStringUtils.randomAlphabetic(10);
        var uuid = RandomStringUtils.randomAlphabetic(10);
        var uuidMono = Mono.just(uuid);
        var status1 = new Status();
        status1.setStatusType(StatusType.IN_PROGRESS);
        status1.setUuid(uuid);
        var statusMono = Mono.just(status1);

        when(processingService.callInit(any())).thenReturn(uuidMono);
        when(processingService.getStatus(any())).thenReturn(statusMono);
        var testResult = Item.builder()
                .status(status1)
                .content(content)
                .build();

        StepVerifier.create(itemService.createItem(content))
                .then(() -> statusMono.subscribe( s -> {
                    status1.setStatusType(StatusType.OK);
                    s.setStatusType(StatusType.OK);
                }))
                .expectNext(testResult)
                .verifyComplete();

        verify(processingService,times(1)).getStatus(any());
    }

}