package com.example.util.alert.controller;

import com.example.util.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@EnableDiscoveryClient
public class AlertController {

    private final AlertService alertService;

    @Scheduled(cron = "0 50 * * * ?")
    @GetMapping("/users")
    public void sendMessageViaSlack() {
        alertService.sendMessageToUsers();
    }
}
