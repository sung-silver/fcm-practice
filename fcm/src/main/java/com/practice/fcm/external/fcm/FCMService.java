package com.practice.fcm.external.fcm;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.auth.oauth2.*;
import com.google.common.net.*;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import com.practice.fcm.common.exception.*;
import com.practice.fcm.domain.*;
import java.io.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import okhttp3.*;
import okhttp3.MediaType;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final ObjectMapper objectMapper;  // FCM의 body 형태에 따라 생성한 값을 문자열로 저장하기 위한 Mapper 클래스
    // SERVICE_ACCOUNT_JSON: 비공개 키를 읽어 헤더에 실어야 push 알림 요청을 정상적으로 보낼 수 있음
    @Value("${fcm.key.path}") private String SERVICE_ACCOUNT_JSON;
    // FCM_API_URL: 미리 생성한 파이어베이스 프로젝트 서버에 요청을 보낼 URL
    @Value("${fcm.api.url}") private String FCM_API_URL;
    // topic: 애플리케이션 내에서 관리하는 주제 (유저들이 구독하는 주제), 해당 주제를 구독하는 유저에게 일괄적으로 푸시 알림 전송 가능
    @Value("${fcm.topic}") private String topic;
    @Value("${fcm.scope}") private String scope;

    /**
     * 단일 기기
     * - Firebase에 메시지를 수신하는 함수 (헤더와 바디 직접 만들기)
     */
    @Transactional
    public void pushAlarm(FCMPushRequest request) {
        String message = makeSingleMessage(request);
        sendPushMessage(message);
    }

    /**
     * 다수 기기
     * - Firebase에 메시지를 수신하는 함수 (동일한 메시지를 2명 이상의 유저에게 발송)
     */
    public void multipleSendByToken(FCMPushRequest request, List<Member> members, String topic) {
        // Member 리스트에서 FCM 토큰만 꺼내와서 리스트로 저장
        List<String> tokens = members.stream()
                .map(Member::getFcmToken).toList();
        MulticastMessage message = makeMultipleMessage(request, tokens, topic);
        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("다수 기기 알림 전송 성공 ! successCount: " + response.getSuccessCount() + " messages were sent successfully");
            log.info("알림 전송: {}", response.getResponses().toString());
        } catch (FirebaseMessagingException e) {
            log.error("다수기기 푸시메시지 전송 실패 - FirebaseMessagingException: {}", e.getMessage());
            throw new IllegalArgumentException(ErrorType.FAIL_TO_SEND_PUSH_ALARM.getMessage());
        }
    }

    /**
     * - 특정 타깃 토큰 없이 해당 주제를 구독한 모든 유저에 푸시 전송 -> 주제를 구독, 주제 구독 취소 기능도 필요
     */
    @Transactional
    public String pushTopicAlarm(FCMPushRequest request) {
        String message = makeTopicMessage(request);
        sendPushMessage(message);
        return "알림을 성공적으로 전송했습니다. targetUserId = " + request.targetToken();
    }

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드 for 단일 기기
    private String makeSingleMessage(FCMPushRequest request) {
        try {
            Notification notification = getNotification(request);
            Message message = Message.builder()
                    .setNotification(notification)
                    .build();
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 처리 도중에 예외가 발생했습니다.");
        }
    }

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드 for 주제 구독을 한 유저
    private String makeTopicMessage(FCMPushRequest request) {
        try {
            Notification notification = getNotification(request);
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(notification)
                    .build();
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 처리 도중에 예외가 발생했습니다.");
        }
    }

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드 for 다수 기기
    private static MulticastMessage makeMultipleMessage(FCMPushRequest request, List<String> tokenList, String topic) {
        Notification notification = getNotification(request);
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(tokenList)
                .build();

        log.info("message: {}", request.title() +" "+ request.body());
        return message;
    }

    private static Notification getNotification(FCMPushRequest request){
        return Notification.builder()
                .setTitle(request.title())
                .setBody(request.body())
                .setImage(request.image())
                .build();
    }

    // 실제 파이어베이스 서버로 푸시 메시지를 전송하는 메서드
    private void sendPushMessage(String message) {

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request httpRequest = new Request.Builder()
                    .url(FCM_API_URL) // 파이어 베이스 서버 url 세팅
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(httpRequest).execute();

            log.info("단일 기기 알림 전송 성공 ! successCount: 1 messages were sent successfully");
            assert response.body() != null;
            log.info("알림 전송: {}", response.body().string());
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 데 실패했습니다.");
        }
    }

    // Firebase에서 사용할 수 있는 accessToken 가져오는 로직
    private String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
                    .createScoped(List.of(scope)); // 범위가 여러개라면 yml에 배열 형태로 명시하고 배열로 가져올 수 있음
            googleCredentials.refreshIfExpired();
            log.info("getAccessToken() - googleCredentials: {} ", googleCredentials.getAccessToken().getTokenValue());

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 데 실패했습니다.");
        }
    }
}
