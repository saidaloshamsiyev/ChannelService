package org.example.channelservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.channelservice.domain.dto.request.SubscriptionRequest;
import org.example.channelservice.domain.dto.response.SubscriptionResponse;
import org.example.channelservice.repository.SubscriptionRepository;
import org.example.channelservice.service.subscription.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {
     private final SubscriptionService subscriptionService;

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponse> create(@RequestBody SubscriptionRequest subscriptionRequest) {
        SubscriptionResponse response = subscriptionService.create(subscriptionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<SubscriptionResponse> getSubscriptionById(@PathVariable UUID id) {
//        SubscriptionResponse response = subscriptionService.findById(id);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        subscriptionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionResponse> getAllSubscriptionsByUserId(@PathVariable UUID userId) {
        SubscriptionResponse response = subscriptionService.findAllSubsBySubsId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
