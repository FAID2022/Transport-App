package com.app;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic busPositionsTopic() {
        return TopicBuilder.name("bus-positions") // Le nom du topic
                .partitions(3)            // ⚡ 3 Partitions (C'est ça le secret !)
                .replicas(1)              // 1 Réplica (car on a 1 seul broker en local)
                .build();
    }
}