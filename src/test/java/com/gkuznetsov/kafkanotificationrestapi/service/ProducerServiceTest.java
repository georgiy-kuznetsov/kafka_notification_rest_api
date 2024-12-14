package com.gkuznetsov.kafkanotificationrestapi.service;

import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import com.gkuznetsov.kafkanotificationrestapi.util.KafkaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {
    @Mock
    private KafkaUtil kafkaUtil;

    @InjectMocks
    private ProducerService producerService;

    @Test
    @DisplayName("Test add notification")
    public void givenNotificationDto_whenAddNotification_thenSendToKafka() {
        // given
        NotificationDto notificationDto = DataUtil.getInternalNotificationDto();

        BDDMockito.given( kafkaUtil.send( any(NotificationDto.class) ) ).willReturn( Mono.empty() );

        // when
        producerService.sendMessage(notificationDto).block();

        // then
        BDDMockito.verify( kafkaUtil, times(1) ).send( any(NotificationDto.class) );
    }
}
