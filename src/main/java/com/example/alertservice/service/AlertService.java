package com.example.alertservice.service;

import com.example.alertservice.UserInfoDto;
import com.example.alertservice.dto.response.AlertResponseDto;
import com.example.alertservice.dto.response.ApplicantListResponseDto;

import java.util.LinkedHashMap;
import java.util.List;

public interface AlertService {
    UserInfoDto getSlackIdByEmail(String email);
    void sendMessageToUser(String email);
    List<LinkedHashMap<String, Object>> getPostInfo();
    List<List<ApplicantListResponseDto>> getUserInfo(long postId);
    void sendMessageToUsers();


    List<List<ApplicantListResponseDto>> transformResponse(List<List<LinkedHashMap<String, Object>>> data);
    ApplicantListResponseDto mapToYourDto(LinkedHashMap<String, Object> map);
}
