package com.kyc.notification.consumer;

import com.kyc.notification.dto.KycEvent;
import com.kyc.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KycEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "${kafka.topics.kyc-events:kyc-events}",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeKycEvent(
            @Payload KycEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received KYC event from topic={}, partition={}, offset={}: type={}, userId={}",
            topic, partition, offset, event.getEventType(), event.getUserId());

        try {
            notificationService.processKycEvent(event);
        } catch (Exception e) {
            log.error("Error processing KYC event for userId={}: {}", event.getUserId(), e.getMessage(), e);
            // In production: send to dead-letter topic
        }
    }
}
