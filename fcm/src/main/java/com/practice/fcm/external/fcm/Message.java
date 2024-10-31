package com.practice.fcm.external.fcm;

/**
 * @param notification  모든 모바일 OS에 통합으로 사용할 수 있는 Notification
 * @param token 특정 디바이스(클라이언트)에 알림을 보내기 위한 토큰
 * @param topic 주제 구독 시 사용
 * **/
public record Message(Notification notification, String token, String topic) {
    public static Message of(Notification notification, String token, String topic) {
        return new Message(notification, token, topic);
    }

    public static Message of(Notification notification, String token) {
        return new Message(notification, token, null);
    }
}
