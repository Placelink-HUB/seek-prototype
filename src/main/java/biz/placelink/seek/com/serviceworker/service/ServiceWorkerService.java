/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
package biz.placelink.seek.com.serviceworker.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import biz.placelink.seek.com.serviceworker.vo.SubscriptionVO;
import groovy.util.logging.Slf4j;
import jakarta.annotation.PostConstruct;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.util.S2JsonUtil;
import kr.s2.ext.util.S2Util;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Slf4j
@Service
public class ServiceWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceWorkerService.class);

    private PushService pushService;

    private final ServiceWorkerMapper serviceWorkerMapper;

    /**
     * VAPID 공개키
     */
    @Value("${web.push.vapid.public}")
    private String publicKey;

    /**
     * VAPID 개인키
     */
    @Value("${web.push.vapid.private}")
    private String privateKey;

    /**
     * VAPID 개인키
     */
    @Value("${web.push.isTest}")
    private boolean isTest;

    ServiceWorkerService(ServiceWorkerMapper serviceWorkerMapper) {
        this.serviceWorkerMapper = serviceWorkerMapper;
    }

    /**
     * 서비스 초기화를 수행합니다. BouncyCastle 보안 프로바이더를 추가하고 PushService 를 초기화합니다.
     *
     * @throws GeneralSecurityException 보안 관련 예외 발생 시
     */
    @PostConstruct
    private void init() throws GeneralSecurityException {
        if (S2Util.isNotEmpty(publicKey) && S2Util.isNotEmpty(privateKey)) {
            Security.addProvider(new BouncyCastleProvider());
            pushService = new PushService(publicKey, privateKey, "mailto:eseungsu@placelink.shop");
        }
    }

    /**
     * 새로운 구독자 정보를 추가합니다.
     *
     * @param subscriptionVO 구독 정보 객체
     */
    public void subscribe(SubscriptionVO subscriptionVO) {
        int result = 0;
        if (this.isValidSubscription(subscriptionVO)) {
            // subscriptionVO.setUserId(SessionHelper.getUserId());
            result = serviceWorkerMapper.mergeSubscription(subscriptionVO);
        }
        if (result == 0) {
            throw new S2RuntimeException("서비스워커 구독 실패");
        }
    }

    private boolean isValidSubscription(SubscriptionVO subscriptionVO) {
        // 구독 정보의 유효성 검사
        return subscriptionVO.keys != null &&
                S2Util.isNotEmpty(subscriptionVO.keys.p256dh) &&
                S2Util.isNotEmpty(subscriptionVO.keys.auth) &&
                S2Util.isNotEmpty(subscriptionVO.endpoint) &&
                (isTest || subscriptionVO.endpoint.startsWith("https://"));
    }

    /**
     * 전체 사용자들에게 푸시 알림을 전송합니다.
     *
     * @param messageMap 전송할 알림 메시지 Map
     */
    public void sendNotificationAll(Map<String, Object> messageMap) {
        this.sendNotification(messageMap, new Long[] {});
    }

    /**
     * 지정된 사용자들에게 푸시 알림을 전송합니다.
     *
     * @param messageMap 전송할 알림 메시지 Map
     * @param userIdArr  알림을 받을 사용자 관리 번호 배열
     */
    private void sendNotification(Map<String, Object> messageMap, Long... userIdArr) {
        if (messageMap != null) {
            messageMap.put("pushId", UUID.randomUUID().toString());

            String jsonMessage = S2JsonUtil.toJsonString(messageMap);
            this.sendNotification(jsonMessage, userIdArr);
        }
    }

    /**
     * 지정된 사용자들에게 푸시 알림을 전송합니다.
     *
     * @param jsonMessage 전송할 알림 메시지 (JSON 문자열)
     * @param userIdArr   알림을 받을 사용자 관리 번호 배열
     */
    private void sendNotification(String jsonMessage, Long... userIdArr) {
        if (S2Util.isNotEmpty(jsonMessage)) {
            CompletableFuture.runAsync(() -> {
                List<SubscriptionVO> subscriptionList = serviceWorkerMapper.selectSubscriptionList(userIdArr);
                if (subscriptionList != null) {
                    CompletableFuture.allOf(subscriptionList.stream()
                            .filter(Objects::nonNull)
                            .map(subscription -> CompletableFuture.runAsync(() -> {
                                try {
                                    Notification notification = new Notification(subscription, jsonMessage);
                                    pushService.send(notification);
                                } catch (GeneralSecurityException | IOException | InterruptedException | JoseException | ExecutionException e) {
                                    logger.error("sendNotification 에러 발생", e);
                                }
                            })).toArray(CompletableFuture[]::new)).join();
                }
            });
        }
    }

    /**
     * 구독자 정보를 제거합니다.
     *
     * @param userIdArr 구독 취소할 사용자 관리 번호 배열
     */
    public void unsubscribe(Long... userIdArr) {
        serviceWorkerMapper.deleteSubscription(userIdArr);
    }

}
