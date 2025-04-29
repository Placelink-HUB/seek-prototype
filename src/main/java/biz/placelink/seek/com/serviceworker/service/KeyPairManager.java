package biz.placelink.seek.com.serviceworker.service;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용

 *  ------------       --------    ---------------------------

 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
public class KeyPairManager {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 키 쌍을 생성할 알고리즘의 표준 이름
     *
     * @ex (필수, "ECDSA", "RSA", "DSA", "ECDH", "EC"...)
     */
    public static final String KEY_ALGORITHM = "ECDSA";
    /**
     * 사용할 보안 제공자의 이름
     *
     * @ex (선택: null이면 시스템에 등록된 제공자 중 해당 알고리즘을 지원하는 첫 번째 제공자를 사용, "BC", "SunJCE", "SunRsaSign"...)
     */
    public static final String KEY_PROVIDER = "BC";
    /**
     * Java 암호화 API에서 타원 곡선(Elliptic Curve) 도메인 매개변수를 생성하기 위한 사양을 정의하는 클래스
     */
    public static final String EC_SPEC = "secp256r1";

    /**
     * Web Push 프로토콜에 필요한 VAPID 키를 가져온다.
     *
     * @ex 서버 재시작 포함 단 한번만 생성하여 사용하고 privateKey는 절대로 클라이언트에 노출해서는 안된다.
     * @ex 최초 생성: KeyPairManager.getVapidKeyMap(KeyPairManager.generateKeyPair());
     * @return
     */
    public static Map<String, String> getVapidKeyMap(KeyPair keyPair) {
        Map<String, String> vapidKeyMap = new HashMap<>();

        if (keyPair != null) {
            String publicKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyPair.getPrivate().getEncoded());

            vapidKeyMap.put("publicKey", publicKey);
            vapidKeyMap.put("privateKey", privateKey);
        }

        return vapidKeyMap;
    }

    /**
     * KeyPair를 생성한다.
     *
     * @return
     */
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyPairManager.KEY_ALGORITHM, KeyPairManager.KEY_PROVIDER);
            keyPairGenerator.initialize(new ECGenParameterSpec(KeyPairManager.EC_SPEC));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException e) {
            keyPair = null;
        }

        return keyPair;
    }

    /**
     * 이미 생성된 공개키와 비공개키 문자열을 KeyPair 객체로 변환한다.
     *
     * @param publicKeyString
     * @param privateKeyString
     * @return
     * @throws Exception
     */
    public static KeyPair convertToKeyPair(String publicKeyString, String privateKeyString) {
        KeyPair keyPair = null;

        PublicKey publicKey = KeyPairManager.stringToPublicKey(publicKeyString);
        PrivateKey privateKey = KeyPairManager.stringToPrivateKey(privateKeyString);

        if (publicKey != null && privateKey != null) {
            keyPair = new KeyPair(publicKey, privateKey);

        }
        return keyPair;
    }

    public static PublicKey stringToPublicKey(String publicKeyString) {
        PublicKey key = null;
        try {
            byte[] publicKeyBytes = Base64.getUrlDecoder().decode(publicKeyString);
            ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(KeyPairManager.EC_SPEC);
            ECPoint point = params.getCurve().decodePoint(publicKeyBytes);
            ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, params);
            KeyFactory kf = KeyFactory.getInstance(KeyPairManager.KEY_ALGORITHM, KeyPairManager.KEY_PROVIDER);
            key = kf.generatePublic(pubSpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            key = null;
        }
        return key;
    }

    public static PrivateKey stringToPrivateKey(String privateKeyString) {
        PrivateKey key = null;
        try {
            byte[] privateKeyBytes = Base64.getUrlDecoder().decode(privateKeyString);
            ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(KeyPairManager.EC_SPEC);
            BigInteger privateKeyValue = new BigInteger(1, privateKeyBytes);
            ECPrivateKeySpec privSpec = new ECPrivateKeySpec(privateKeyValue, params);
            KeyFactory kf = KeyFactory.getInstance(KeyPairManager.KEY_ALGORITHM, KeyPairManager.KEY_PROVIDER);
            key = kf.generatePrivate(privSpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            key = null;
        }
        return key;
    }

}
