package com.example.alertservice.alert.service;

import com.example.alertservice.alert.dto.UserInfoDto;
import com.example.alertservice.alert.dto.response.ApplicantListResponseDto;
import com.example.alertservice.common.SuccessApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AlertServiceImpl implements AlertService {

    @Value("${slack.app.token}")
    private String token;

    @Value("${miracle.privateKey}")
    private String privateKey;

    /**
     * @param email
     * 이메일을 받아서 슬랙에서 정보를 받아옵니다.
     */
    public UserInfoDto getSlackIdByEmail(String email) {
        String url = "https://slack.com/api/users.lookupByEmail";
        url += "?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );
        JSONObject body = new JSONObject(responseEntity.getBody());
        if (!body.has("user")) {
            return new UserInfoDto("default", "default", "default");
        }

        String id = body.getJSONObject("user").getString("id");
        String realName = body.getJSONObject("user").getString("real_name");
        String name = body.getJSONObject("user").getString("name");

        return new UserInfoDto(id, realName, name);
    }

    /**
     * 알림 서비스의 메인 서비스
     * 공고 정보와 해당 공고에 지원한 지원자 정보를 받아와서 슬랙 알림을 보냅니다.
     */
    public void sendMessageToUsers() {
        List<LinkedHashMap<String, Object>> postList = getPostInfo();

        String url = "https://slack.com/api/chat.postMessage";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json; charset=UTF-8");

        for (int i = 0; i < postList.size(); i++) {
            Integer pId = (Integer) postList.get(i).get("id");

            String postTitle = (String) postList.get(i).get("title");
            long postId = (long)pId;
            String companyName = (String) postList.get(i).get("name");
            String testStartDate = (String) postList.get(i).get("testStartDate");
            String testEndDate = (String) postList.get(i).get("testEndDate");
            String[] testStart = testStartDate.split("T");
            String[] testEnd = testEndDate.split("T");

            List<List<ApplicantListResponseDto>> userInfo = getUserInfo(postId);
            userInfo.iterator().forEachRemaining((List<ApplicantListResponseDto> innerList) -> {
                innerList.iterator().forEachRemaining((ApplicantListResponseDto dto) -> {
                    String name = dto.getName();
                    String email = dto.getEmail();
                    if (email.contains("google#")) {
                        String[] split = email.split("#");
                        email = split[0];
                    }

                    UserInfoDto userInfoDto = getSlackIdByEmail(email);
                    if (!userInfoDto.getId().equals("default")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("channel", userInfoDto.getId());
                        jsonObject.put("text", "🔔 NOTICE 🔔\n" +name + " 님, 지원하신 " + companyName +"의 코딩 테스트가 10분 뒤에 오픈 예정입니다.\n" +
                                "잊지 말고 참여해주세요.\n\n" +
                                "🏢 기업명 : " + companyName + "\n" +
                                "📄 공고명 : " + postTitle + "\n" +
                                "🖥 코딩테스트 시작일 : " + testStart[0] + " 오픈 시간 : " + testStart[1] + "\n" +
                                "⌨️ 코딩테스트 종료일 : " + testEnd[0] + " 종료 시간 : " + testEnd[1] );
                        String body = jsonObject.toString();

                        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                        RestTemplate restTemplate = new RestTemplate();

                        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                    }

                });
            });
        }
    }


    /**
     *  현재 시간(KST)을 기준으로 10분 뒤 코딩테스트가 진행예정인 공고 정보를 받아옵니다.
     */
    public List<LinkedHashMap<String, Object>> getPostInfo() {
        String sendingKey = UUID.randomUUID().toString();
        HttpHeaders headers = new HttpHeaders();
        String miracle = sendingKey + privateKey;
        String miracleToken = String.valueOf(miracle.hashCode());
        headers.add("Session-Id", sendingKey);
        headers.add("Miracle", miracleToken);

        String url = "http://13.125.211.61:60002/v1/company/posts";
        //String url = "http://localhost:60002/v1/company/posts";

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<SuccessApiResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                SuccessApiResponse.class
        );
        List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>) responseEntity.getBody().getData();
        return data;
    }


    /**
     * postId를 통해 해당 공고에 지원한 지원자 리스트를 받아옵니다.
     */
    public List<List<ApplicantListResponseDto>> getUserInfo(long postId) {
        String sendingKey = UUID.randomUUID().toString();
        HttpHeaders headers = new HttpHeaders();
        String miracle = sendingKey + privateKey;
        String miracleToken = String.valueOf(miracle.hashCode());
        headers.add("Session-Id", sendingKey);
        headers.add("Miracle", miracleToken);

        String url = "http://3.36.113.249:60001/v1/post/"+postId+"/applicant/list";
        //String url = "http://localhost:60001/v1/post/"+postId+"/applicant/list";

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<SuccessApiResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                SuccessApiResponse.class
        );
        List<List<LinkedHashMap<String, Object>>> data = (List<List<LinkedHashMap<String, Object>>>) responseEntity.getBody().getData();
        return transformResponse(data);
    }

    /**
     * 지원자 리스트를 DTO로 매칭하여 반환합니다.
     */
    public List<List<ApplicantListResponseDto>> transformResponse(List<List<LinkedHashMap<String, Object>>> data) {
        // 변환 로직을 작성합니다.
        // 예를 들어, 각 Map을 YourDto로 변환하는 작업을 수행합니다.
        // 여기서 YourDto는 실제로 사용하려는 데이터 모델 클래스입니다.
        // 아래는 예시로 YourDto가 어떤 구조인지 표현한 것일 뿐 실제로는 해당 클래스를 정의해야 합니다.
        List<List<ApplicantListResponseDto>> result = new ArrayList<>();

        for (List<LinkedHashMap<String, Object>> innerList : data) {
            List<ApplicantListResponseDto> innerResult = new ArrayList<>();
            if (innerList.size() == 0) {
                return result;
            }
            for (LinkedHashMap<String, Object> map : innerList) {
                ApplicantListResponseDto yourDto = mapToYourDto(map);
                innerResult.add(yourDto);
            }
            result.add(innerResult);
        }
        return result;
    }


    /**
     * LinkedHashMap을 DTO 매핑
     */
    public ApplicantListResponseDto mapToYourDto(LinkedHashMap<String, Object> map) {
        // Map을 YourDto로 변환하는 로직을 작성합니다.
        // 예를 들어, ObjectMapper를 사용하여 매핑할 수 있습니다.
        // 아래는 예시로 ObjectMapper를 사용하는 코드입니다.
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, ApplicantListResponseDto.class);
    }

}
