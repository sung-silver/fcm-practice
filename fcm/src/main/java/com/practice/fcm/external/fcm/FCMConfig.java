package com.practice.fcm.external.fcm;

import static com.practice.fcm.common.exception.ErrorType.FIREBASE_CONNECTION_ERROR;

import com.google.auth.oauth2.*;
import com.google.firebase.*;
import com.google.firebase.messaging.*;
import com.practice.fcm.common.exception.*;
import java.io.*;
import javax.annotation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.*;

@Configuration
@Slf4j
public class FCMConfig {
    @Value("${fcm.key.path}")
    private String SERVICE_KEY_FILE_PATH;
    private FirebaseApp firebaseApp;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(SERVICE_KEY_FILE_PATH);
            InputStream serviceAccount = resource.getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
            log.info("connect to firebase success");
        } catch (IOException e) {
            log.error("connect to firebase fail");
            throw new CustomException(FIREBASE_CONNECTION_ERROR);
        }
    }

    @Bean
    FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
