package com.example.alertservice.controller;

import com.example.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @Scheduled(cron = "0 0 7 * * ?")
    @GetMapping("/users")
    public void test3() {
        alertService.sendMessageToUsers();
    }
}
