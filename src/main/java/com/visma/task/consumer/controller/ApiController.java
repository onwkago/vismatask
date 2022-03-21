package com.visma.task.consumer.controller;

import com.visma.task.consumer.model.Item;
import com.visma.task.consumer.service.ItemService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiController {

    ItemService itemService;

    @GetMapping("/getitem")
    public ResponseEntity<Mono<Item>> getStatus() {
        Mono<Item> item = itemService.createItem("Content");
        return new ResponseEntity(item, HttpStatus.OK);
    }

}
