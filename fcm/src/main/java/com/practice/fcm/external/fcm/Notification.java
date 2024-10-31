package com.practice.fcm.external.fcm;

public record Notification(String title, String body, String image) {
    public static Notification of(String title, String body, String image) {
        return new Notification(title, body, image);
    }

    public static Notification of(String title, String body) {
        return new Notification(title, body, null);
    }
}
