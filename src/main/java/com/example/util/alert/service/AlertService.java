package com.example.util.alert.service;


import com.example.util.alert.dto.UserInfoDto;
import com.example.util.alert.dto.response.ApplicantListResponseDto;

import java.util.LinkedHashMap;
import java.util.List;

public interface AlertService {
    UserInfoDto getSlackIdByEmail(String email);
    List<LinkedHashMap<String, Object>> getPostInfo();
    List<List<ApplicantListResponseDto>> getUserInfo(long postId);
    void sendMessageToUsers();


    List<List<ApplicantListResponseDto>> transformResponse(List<List<LinkedHashMap<String, Object>>> data);
    ApplicantListResponseDto mapToYourDto(LinkedHashMap<String, Object> map);
}
