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
