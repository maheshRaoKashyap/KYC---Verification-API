package com.kyc.kyc.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycEventProducer {

    private final KafkaTemplate<String, KycEvent> kafkaTemplate;

    @Value("${kafka.topics.kyc-events:kyc-events}")
    private String kycEventsTopic;

    public void publishKycEvent(KycEvent event) {
        String key = "user-" + event.getUserId();
        CompletableFuture<SendResult<String, KycEvent>> future =
            kafkaTemplate.send(kycEventsTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("KYC event published: type={}, userId={}, partition={}, offset={}",
                    event.getEventType(), event.getUserId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish KYC event: type={}, userId={}, error={}",
                    event.getEventType(), event.getUserId(), ex.getMessage());
            }
        });
    }
}
