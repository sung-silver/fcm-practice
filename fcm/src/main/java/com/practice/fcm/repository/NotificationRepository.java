package com.practice.fcm.repository;

import com.practice.fcm.domain.*;
import org.springframework.data.jpa.repository.*;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
