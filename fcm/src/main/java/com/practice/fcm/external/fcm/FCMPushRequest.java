package com.practice.fcm.external.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FCMPushRequest(
        String targetToken,
        String title,
        String body,
        String image
) {
    public static FCMPushRequest of(String title, String body, String targetToken) {
        return new FCMPushRequest(
                targetToken,
                title,
                body,
                null // image는 null로 설정
        );
    }
}
