package fr.mbds.securesms.kryptos;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class KryptosAES {

    private KeyGenerator keyGenerator;

    {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String chiffrement(final String plain, PublicKey publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                    BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes());

        System.out.println("Chiffré : " + new String(encryptedBytes));
        Log.w("[chiffrement public]", "-------------" + new String(encryptedBytes));
        return new String(encryptedBytes);
    }

    public String dechiffrement(String plain, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                    BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(plain.getBytes());

        System.out.println("Déchiffré : " + new String(decryptedBytes));
        Log.w("[dechiffrement privee]", "-------------" + new String(decryptedBytes));
        return new String(decryptedBytes);
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
