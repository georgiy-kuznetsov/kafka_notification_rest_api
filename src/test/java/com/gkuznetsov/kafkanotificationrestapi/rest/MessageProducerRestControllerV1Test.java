package com.gkuznetsov.kafkanotificationrestapi.rest;


import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.service.ProducerService;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ComponentScan(basePackages = {
        "com.gkuznetsov.kafkanotificationrestapi.config",
        "com.gkuznetsov.kafkanotificationrestapi.mapper",
        "com.gkuznetsov.kafkanotificationrestapi.util"
})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {MessageProducerRestControllerV1.class})
public class MessageProducerRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private ProducerService producerService;

    @Test
    public void test() {
        // given
        CreateNotificationRequestDto requestDto = DataUtil.getCreateInternalNotificationRequestDto();

        BDDMockito.given( producerService.sendMessage(any(NotificationDto.class)) )
                .willReturn( Mono.empty() );

        // when
        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/kafka-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), CreateNotificationRequestDto.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isOk();
    }
}
