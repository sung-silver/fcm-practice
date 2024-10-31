package com.practice.fcm.domain;

import com.practice.fcm.common.base.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;
    /*
    *알림에 대상이 되는 엔티티가 있다면 추가
    * 보통 댓글에 대한 알림이라면 다음과 같이 작성
    * @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "comment_id")
    * private Comment comment;
    */
    private String message; // 알림 전송에 사용된 내용
    private Boolean isRead = false; // 알림 확인 여부
}
