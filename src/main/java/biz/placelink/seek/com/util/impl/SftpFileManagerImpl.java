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
package biz.placelink.seek.com.util.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.s2.ext.file.impl.S2SftpFileManagerImpl;

/**
 * <pre>
 * SFTP 파일 전송 서비스 파일 업로드, 다운로드, 삭제 및 디렉토리 생성 기능을 제공
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 01.      s2          최초생성
 * </pre>
 */
@Component
public class SftpFileManagerImpl extends S2SftpFileManagerImpl {

    Integer sessionMaxTotal = 5;

    public SftpFileManagerImpl(
            @Value("${sftp.host}") String host,
            @Value("${sftp.port}") int port,
            @Value("${sftp.user}") String username,
            @Value("${sftp.private-key-path}") String privateKeyPath,
            @Value("${sftp.private-key-passphrase}") String passphrase,
            @Value("${sftp.password}") String password,
            @Value("${sftp.session.max-total}") Integer sessionMaxTotal,
            @Value("${sftp.session.min-idle}") Integer sessionMinIdle,
            @Value("${sftp.session.max-wait-millis}") Integer sessionMaxWaitMillis) {
        super(host, port, username, privateKeyPath, passphrase, password, sessionMaxTotal, sessionMinIdle, sessionMaxWaitMillis);
    }

}
