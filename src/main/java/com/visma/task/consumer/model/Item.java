package com.visma.task.consumer.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Item {

    private String content;
    private Status status;

}
