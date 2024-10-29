package com.practice.fcm.external.config;

import com.google.auth.oauth2.*;
import com.google.firebase.*;
import java.io.*;
import javax.annotation.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.*;

@Configuration
@Slf4j
public class FCMConfig {
    @Value("${fcm.key.path}")
    private String SERVICE_KEY_FILE_PATH;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(SERVICE_KEY_FILE_PATH);
            InputStream serviceAccount = resource.getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("connect to firebase success");
        } catch (IOException e) {
            log.error("connect to firebase fail");
        }
    }
}
