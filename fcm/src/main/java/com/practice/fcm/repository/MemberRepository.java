package com.practice.fcm.repository;

import com.practice.fcm.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByGroupId(Long groupId);
    Optional<Member> findByFcmToken(String FcmToken);
}
