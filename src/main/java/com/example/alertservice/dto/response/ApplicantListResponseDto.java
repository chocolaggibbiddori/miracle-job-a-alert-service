package com.example.alertservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApplicantListResponseDto {

    private final Long applicationLetterId;
    private final Long userId;
    private final String resumeTitle;
    private final String email;
    private final String name;
    private final String address;
    private final String submitDate;

    public ApplicantListResponseDto(Long applicationLetterId, Long userId, String resumeTitle, String email, String name, String address, LocalDateTime submitDate) {
        this.applicationLetterId = applicationLetterId;
        this.userId = userId;
        this.resumeTitle = resumeTitle;
        this.email = email;
        this.name = name;
        this.address = address;
        this.submitDate = formatDate(submitDate);
    }


    public ApplicantListResponseDto() {
        this.applicationLetterId = null;
        this.userId = null;
        this.resumeTitle = null;
        this.email = null;
        this.name = null;
        this.address = null;
        this.submitDate = null;
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
