const notiIcon = 'https://goono.placelink.biz/lib/eln/img/img_gnb_profile.svg';
const notiOption = {
    tag: 'pl-notification',
    icon: notiIcon,
    badge: notiIcon,
    vibrate: [200, 100, 200]
};

self.addEventListener('push', function (event) {
    console.debug('Push event received:', event, event.data);
    const jsonData = event.data.json();
    const pushTypeCcd = jsonData.pushTypeCcd;
    const message = jsonData.message;

    if (message) {
        notiOption['body'] = message;
        if (jsonData.link) {
            notiOption['data'] = {
                link: jsonData.link
            };

            // link 가 있다면 추가적인 액션을 할 수 있는 버튼 추가
            notiOption['actions'] = [
                {action: 'explore', title: '자세히 보기'},
                {action: 'close', title: '닫기'}
            ];
        }

        event.waitUntil(
            // 서비스워커 알림(Notification) 사용
            self.registration.showNotification('【SEEK】', notiOption)
        );
    }

    event.waitUntil(
        // 클라이언트 메인 스크립트 토스트 사용
        self.clients.matchAll({includeUncontrolled: true, type: 'window'}).then((clientList) => {
            clientList.forEach((client) => {
                client.postMessage({
                    type: pushTypeCcd,
                    message: message
                });
            });
        })
    );
});

// 알림 클릭 시 동작 정의
self.addEventListener('notificationclick', function (event) {
    event.notification.close(); // 알림 닫기
    let openLink = event.notification.data && event.notification.data.link ? event.notification.data.link : 'https://goono.placelink.biz';
    if (!openLink.startsWith('http://') && !openLink.startsWith('https://')) {
        openLink = `https://${openLink}`;
    }

    switch (event.action) {
        case 'close':
            // '닫기' 액션 처리
            break;
        case 'explore':
        default:
            // 알림 자체를 클릭한 경우(action이 없는 경우) 액션 처리
            event.waitUntil(
                self.clients.matchAll({includeUncontrolled: true, type: 'window'}).then((clientList) => {
                    const openUrl = new URL(openLink);

                    for (const client of clientList) {
                        // 클라이언트에서 같은 도메인의 탭이 있는지 확인한다.
                        if (new URL(client.url).origin === openUrl.origin) {
                            // 같은 도메인의 탭을 찾았으면 해당 탭에서 URL 열기
                            return client
                                .navigate(openUrl.href)
                                .then((client) => client.focus())
                                .catch((err) => {
                                    console.error('탭을 여는 중 오류 발생:', err);
                                    // 오류가 발생하면 새 탭에서 열기
                                    return self.clients.openWindow(openUrl.href);
                                });
                        }
                    }

                    // 같은 도메인의 탭이 없으면 새 탭에서 열기
                    return self.clients.openWindow(openUrl.href);
                })
            );
            break;
    }
});

// 서비스 워커 즉시 활성화
self.addEventListener('install', (event) => {
    event.waitUntil(self.skipWaiting());
});
self.addEventListener('activate', (event) => {
    event.waitUntil(self.clients.claim());
});
