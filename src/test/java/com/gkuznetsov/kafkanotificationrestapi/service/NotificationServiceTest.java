package com.gkuznetsov.kafkanotificationrestapi.service;

import com.gkuznetsov.kafkanotificationrestapi.dto.GetAllNotificationsResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.exception.NotFoundException;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import com.gkuznetsov.kafkanotificationrestapi.util.KafkaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private KafkaUtil kafkaUtil;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Test listen notification and add")
    public void givenNotificationDto_whenNewNotificationArrives_thenNotificationAdded() {
        // given
        NotificationEntity notificationDto = DataUtil.getInternalNotificationEntityTransient();
        NotificationEntity notificationEntity = DataUtil.getInternalNotificationEntityTransient();

        BDDMockito.when( kafkaUtil.receive() ).thenReturn( Flux.just(notificationDto) );
        BDDMockito.when( notificationRepository.existsByUniqueFields(anyString(), anyString(), anyString(), anyString()) )
                .thenReturn( Mono.just(false) );
        BDDMockito.when( notificationRepository.save(any(NotificationEntity.class)) )
                .thenReturn( Mono.just(notificationEntity) );

        // when
        notificationService.startListening();

        // then
        BDDMockito.verify( kafkaUtil, times( 1 ) ).receive();
        BDDMockito.verify( notificationRepository, times( 1 ) )
                .existsByUniqueFields( anyString(), anyString(), anyString(), anyString() );
        BDDMockito.verify( notificationRepository, times( 1 ) ).save( any(NotificationEntity.class) );
    }

    @Test
    @DisplayName("Test listen notification with existent unique fields and not add")
    public void givenNotificationDtoWithExistentUniqueFields_whenNewNotificationArrives_thenNotificationNotAdded() {
        // given
        NotificationEntity notificationDto = DataUtil.getInternalNotificationEntityTransient();
        NotificationEntity notificationEntity = DataUtil.getInternalNotificationEntityTransient();

        BDDMockito.when( kafkaUtil.receive() ).thenReturn( Flux.just(notificationDto) );
        BDDMockito.when( notificationRepository.existsByUniqueFields(anyString(), anyString(), anyString(), anyString()) )
                .thenReturn( Mono.just(true) );

        // when
        notificationService.startListening();

        // then
        BDDMockito.verify( kafkaUtil, times( 1 ) ).receive();
        BDDMockito.verify( notificationRepository, times( 1 ) )
                .existsByUniqueFields( anyString(), anyString(), anyString(), anyString() );
        BDDMockito.verify( notificationRepository, never() ).save( any(NotificationEntity.class) );
    }

    @Test
    @DisplayName("Test get all notifications")
    public void givenTwoNotifications_whenGetAll_thenReturnListOfNotifications() {
        // given
        NotificationEntity notification1 = DataUtil.getInternalNotificationEntityTransient();
        NotificationEntity notification2 = DataUtil.getExternalNotificationEntityTransient();

        NotificationDto notificationDto1 = DataUtil.getInternalNotificationDto();
        NotificationDto notificationDto2 = DataUtil.getExternalNotificationDto();

        BDDMockito.when( notificationRepository.findAllBy(anyInt(), anyInt()) )
                .thenReturn( Flux.just(notification1, notification2) );
        BDDMockito.when( notificationRepository.count() ).thenReturn( Mono.just(2L) );
        BDDMockito.when( notificationMapper.map(notification1) ).thenReturn(notificationDto1);
        BDDMockito.when( notificationMapper.map(notification2) ).thenReturn(notificationDto2);

        // when
        notificationService.getNotifications(1, 24).block();

        // then
        BDDMockito.verify( notificationRepository, times( 1 ) ).findAllBy(anyInt(), anyInt());
        BDDMockito.verify( notificationRepository, times( 1 ) ).count();
    }

    @Test
    @DisplayName("Test get all notifications on not found page")
    public void givenTwoNotifications_whenGetAllOnNotFoundPage_thenThrowNotFoundException() {
        // given
        NotificationEntity notification1 = DataUtil.getInternalNotificationEntityTransient();
        NotificationEntity notification2 = DataUtil.getExternalNotificationEntityTransient();

        NotificationDto notificationDto1 = DataUtil.getInternalNotificationDto();
        NotificationDto notificationDto2 = DataUtil.getExternalNotificationDto();

        BDDMockito.when( notificationRepository.findAllBy(anyInt(), anyInt()) )
                .thenReturn( Flux.just(notification1, notification2) );
        BDDMockito.when( notificationRepository.count() ).thenReturn( Mono.just(2L) );
        BDDMockito.when( notificationMapper.map(notification1) ).thenReturn(notificationDto1);
        BDDMockito.when( notificationMapper.map(notification2) ).thenReturn(notificationDto2);

        // when
        Mono<GetAllNotificationsResponseDto> notificationDtoFlux = notificationService.getNotifications(3, 2);

        // then
        StepVerifier.create(notificationDtoFlux)
                .expectErrorMatches( throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Page not found") )
                .verify();

        BDDMockito.verify( notificationRepository, times( 1 ) ).findAllBy(anyInt(), anyInt());
        BDDMockito.verify( notificationRepository, times( 1 ) ).count();
    }

    @Test
    @DisplayName("Test get notification")
    public void givenNotification_whenGetNotificationById_thenReturnNotification() {
        // given
        NotificationEntity notification = DataUtil.getInternalNotificationEntityTransient();

        BDDMockito.when( notificationRepository.findById(anyLong()) ).thenReturn( Mono.just(notification) );

        // when
        Mono<NotificationEntity> notificationMono = notificationService.getNotificationById(1L);

        // then
        StepVerifier.create(notificationMono)
                .expectNext(notification)
                .verifyComplete();

        BDDMockito.verify( notificationRepository, times( 1 ) ).findById(anyLong());
    }

    @Test
    @DisplayName("Test get notification by nonexistent id")
    public void givenNonExistentId_whenGetNotificationById_thenThrowNotFoundException() {
        // given
        long nonExistentNotificationId = 1L;
        BDDMockito.when( notificationRepository.findById(anyLong()) ).thenReturn( Mono.empty() );

        // when
        Mono<NotificationEntity> userMono = notificationService.getNotificationById(nonExistentNotificationId);

        // then
        StepVerifier.create(userMono)
                .expectErrorMatches( throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Notification not found") )
                .verify();

        BDDMockito.verify( notificationRepository, times(1) ).findById(nonExistentNotificationId);
    }

    @Test
    @DisplayName("Test change status of notification")
    public void givenDataToUpdateUser_whenUpdateUser_thenUpdate() {
        // given
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");
        NotificationEntity notification = DataUtil.getInternalNotificationEntityTransient();

        BDDMockito.when( notificationRepository.findById(anyLong()) )
                .thenReturn( Mono.just(notification) );
        BDDMockito.when( notificationRepository.save(any(NotificationEntity.class)) )
                .thenReturn( Mono.just(notification) );

        // when
        Mono<NotificationEntity> updatedNotification = notificationService.updateNotificationById(1L, requestDto);

        // then
        StepVerifier.create(updatedNotification)
                .expectNext(notification)
                .verifyComplete();

        BDDMockito.verify( notificationRepository, times( 1 ) ).findById( anyLong() );
        BDDMockito.verify( notificationRepository, times( 1 ) ).save( any(NotificationEntity.class) );
    }

    @Test
    @DisplayName("Test change status of notification with nonexistent id")
    public void givenNonexistentUserId_whenUpdateUser_thenThrowNotFoundException() {
        // given
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");
        long nonExistentNotificationId = 1L;

        BDDMockito.when( notificationRepository.findById(anyLong()) ).thenReturn( Mono.empty() );

        // when
        Mono<NotificationEntity> updatedUserMono = notificationService
                .updateNotificationById(nonExistentNotificationId, requestDto);

        // then
        StepVerifier.create(updatedUserMono)
                .expectErrorMatches( throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Notification not found") )
                .verify();

        BDDMockito.verify( notificationRepository, times( 1 ) ).findById( nonExistentNotificationId );
        BDDMockito.verify( notificationRepository, never() ).save( any(NotificationEntity.class) );
    }
}
