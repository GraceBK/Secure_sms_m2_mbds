package fr.mbds.securesms.kryptos;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KryptosAES {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("AES");

    public KryptosAES() throws NoSuchAlgorithmException {
    }
}
