const notificationIcon = 'https://goono.placelink.biz/lib/eln/img/img_gnb_profile.svg';
const defaultNotificationOptions = {
    tag: 'pl-notification',
    icon: notificationIcon,
    badge: notificationIcon,
    vibrate: [200, 100, 200],
    requireInteraction: false // 알림 자동 닫기 허용
};

// 최근 알림 메시지 캐싱
const notificationCache = new Set();

// 알림 타이머 관리
const activeNotifications = new Map();

self.addEventListener('push', function (event) {
    // 푸시 이벤트 수신 처리
    let payload;
    try {
        payload = event.data.json();
    } catch (e) {
        console.error('푸시 데이터 파싱 실패:', event.data.text(), e);
        const notificationOptions = { ...defaultNotificationOptions, body: '새 알림이 도착했습니다.' };
        event.waitUntil(
            self.registration.showNotification('【SEEK】', notificationOptions).then((notification) => {
                // 10초 후 알림 닫기
                const timerId = setTimeout(() => {
                    notification.close();
                    activeNotifications.delete(notification);
                }, 10000);
                activeNotifications.set(notification, timerId);
            }).catch((err) => {
                console.error('알림 표시 실패:', err);
                // 에러를 클라이언트로 전송
                self.clients.matchAll({ includeUncontrolled: true, type: 'window' }).then((clientList) => {
                    clientList.forEach((client) => {
                        client.postMessage({
                            type: 'error',
                            message: '알림 표시 중 오류 발생',
                            error: err.message
                        });
                    });
                });
            })
        );
        return;
    }

    // 페이로드 유효성 검사
    if (!payload || typeof payload !== 'object') {
        console.error('유효하지 않은 페이로드:', payload);
        return;
    }

    const pushTypeCode = payload.pushTypeCode || 'default';
    const message = payload.message || '알림 내용이 없습니다.';
    const title = payload.title || '【SEEK】';
    const locale = payload.locale || 'en'; // 다국어 지원 준비

    // 중복 알림 방지
    const cacheKey = `${title}:${message}`;
    if (notificationCache.has(cacheKey)) {
        console.log('중복 알림 무시:', cacheKey);
        return;
    }
    notificationCache.add(cacheKey);
    // 캐시 크기 제한 (최대 50개)
    if (notificationCache.size > 50) {
        const oldest = notificationCache.values().next().value;
        notificationCache.delete(oldest);
    }

    // 알림 옵션 동적 생성
    const notificationOptions = { ...defaultNotificationOptions };
    notificationOptions.body = locale === 'ko' ? message : `Notification: ${message}`; // 다국어 처리
    notificationOptions.tag = `pl-notification-${Date.now()}`; // 고유 태그
    if (payload.link) {
        notificationOptions.data = { link: payload.link };
        notificationOptions.actions = [
            { action: 'explore', title: locale === 'ko' ? '자세히 보기' : 'View Details' },
            { action: 'close', title: locale === 'ko' ? '닫기' : 'Close' }
        ];
    }

    event.waitUntil(
        Promise.all([
            // 알림 표시
            self.registration.showNotification(title, notificationOptions).then((notification) => {
                // 10초 후 알림 닫기
                const timerId = setTimeout(() => {
                    notification.close();
                    activeNotifications.delete(notification);
                }, 10000);
                activeNotifications.set(notification, timerId);
            }).catch((err) => {
                console.error('알림 표시 실패:', err);
                // 에러를 클라이언트로 전송
                self.clients.matchAll({ includeUncontrolled: true, type: 'window' }).then((clientList) => {
                    clientList.forEach((client) => {
                        client.postMessage({
                            type: 'error',
                            message: '알림 표시 중 오류 발생',
                            error: err.message
                        });
                    });
                });
            }),
            // 클라이언트로= window' }).then((clientList) => {
                clientList.forEach((client) => {
                    client.postMessage({
                        type: pushTypeCode,
                        message: message,
                        title: title,
                        data: payload
                    });
                });
            })
        ])
    );
});

self.addEventListener('notificationclick', function (event) {
    // 알림 클릭 처리
    event.notification.close();
    // 활성 타이머 정리
    const timerId = activeNotifications.get(event.notification);
    if (timerId) {
        clearTimeout(timerId);
        activeNotifications.delete(event.notification);
    }

    let openLink = event.notification.data?.link || 'https://goono.placelink.biz';

    if (!openLink.startsWith('http://') && !openLink.startsWith('https://')) {
        openLink = `https://${openLink}`;
    }

    switch (event.action) {
        case 'close':
            break;
        case 'explore':
        default:
            event.waitUntil(
                self.clients.matchAll({ includeUncontrolled: true, type: 'window' }).then((clientList) => {
                    const openUrl = new URL(openLink);
                    // 클라이언트 검색 최적화
                    const matchingClient = clientList.find((client) => new URL(client.url).origin === openUrl.origin);
                    if (matchingClient) {
                        return matchingClient
                            .navigate(openUrl.href)
                            .then((client) => client.focus())
                            .catch((err) => {
                                console.error('탭 이동 실패:', err);
                                return self.clients.openWindow(openUrl.href);
                            });
                    }
                    return self.clients.openWindow(openUrl.href);
                }).catch((err) => {
                    console.error('클라이언트 검색 실패:', err);
                    // 에러를 클라이언트로 전송
                    self.clients.matchAll({ includeUncontrolled: true, type: 'window' }).then((clientList) => {
                        clientList.forEach((client) => {
                            client.postMessage({
                                type: 'error',
                                message: '알림 클릭 처리 중 오류 발생',
                                error: err.message
                            });
                        });
                    });
                })
            );
            break;
    }
});

self.addEventListener('message', function (event) {
    // 클라이언트로부터 메시지 수신 처리
    if (event.data && event.data.type === 'clearNotifications') {
        // 모든 활성 알림 정리
        activeNotifications.forEach((timerId, notification) => {
            clearTimeout(timerId);
            notification.close();
        });
        activeNotifications.clear();
        console.log('모든 알림 타이머 정리 완료:', new Date().toISOString());
    }
});

self.addEventListener('install', (event) => {
    // 서비스 워커 즉시 활성화
    event.waitUntil(self.skipWaiting());
});

self.addEventListener('activate', (event) => {
    // 클라이언트 즉시 제어
    event.waitUntil(self.clients.claim());
});
