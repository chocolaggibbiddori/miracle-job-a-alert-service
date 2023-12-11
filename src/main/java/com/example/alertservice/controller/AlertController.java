package com.example.alertservice.controller;

import com.example.alertservice.service.AlertService;
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
    public void test3() {
        alertService.sendMessageToUsers();
    }
}
