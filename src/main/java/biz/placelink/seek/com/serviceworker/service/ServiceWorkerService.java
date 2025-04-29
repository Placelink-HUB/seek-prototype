package biz.placelink.seek.com.serviceworker.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.Objects;
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
            // subscriptionVO.setUserMno(SessionHelper.getUserMno());
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
                subscriptionVO.endpoint.startsWith("https://");
    }

    /**
     * 지정된 사용자들에게 푸시 알림을 전송합니다.
     *
     * @param message    전송할 알림 메시지
     * @param userMnoArr 알림을 받을 사용자 관리 번호 배열
     */
    public void sendNotification(String message, Long... userMnoArr) {
        if (S2Util.isNotEmpty(message)) {
            CompletableFuture.runAsync(() -> {
                List<SubscriptionVO> subscriptionList = serviceWorkerMapper.selectSubscriptionList(userMnoArr);
                if (subscriptionList != null) {
                    CompletableFuture.allOf(subscriptionList.stream()
                            .filter(Objects::nonNull)
                            .map(subscription -> CompletableFuture.runAsync(() -> {
                                try {
                                    Notification notification = new Notification(subscription, message);
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
     * @param userMnoArr 구독 취소할 사용자 관리 번호 배열
     */
    public void unsubscribe(Long... userMnoArr) {
        serviceWorkerMapper.deleteSubscription(userMnoArr);
    }

}
