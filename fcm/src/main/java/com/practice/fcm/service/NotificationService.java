package com.practice.fcm.service;

import com.practice.fcm.domain.*;
import com.practice.fcm.external.fcm.*;
import com.practice.fcm.repository.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    public void sendSingleNotificationTest() {
        Optional<Member> member = memberRepository.findById(1L);
        if(member.isPresent()){
            Member foundMember = member.get();
            FCMPushRequest request = FCMPushRequest.of("테스트 알람입니다", "알림 테스트", foundMember.getFcmToken());
            fcmService.pushAlarm(request);
            Notification notification = Notification.builder()
                    .member(foundMember)
                    .message(request.body()).build();
            notificationRepository.save(notification);
        }
    }
}
