package com.practice.fcm.external.fcm;

public record FCMMessage(boolean validateOnly, Message message) {
    public static FCMMessage of(boolean validateOnly, Message message) {
        return new FCMMessage(validateOnly, message);
    }
}
