package com.mohamed_kms.securedmessages;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by mohamed-kms on 31/10/18
 **/
public class Cryptography {

    String transformation = "RSA/ECB/PKCS1Padding";
    String provider = "AndroidKeyStore";
    KeyPairGenerator kpg;
    KeyGenParameterSpec.Builder builder;
    KeyPair kp;
    PublicKey publicKey;
    KeyStore keyStore;
    KeyStore.Entry entry;
    PrivateKey privateKey;
    Cipher cipher;
    private byte[] encryptedBytes;
    private byte[] decryptedBytes;
    String alias;

    public Cryptography(String alias) throws NoSuchProviderException {
        {
            try {
                this.alias = alias;
                this.kpg = KeyPairGenerator.getInstance("RSA", provider);
                this.builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
                this.kpg.initialize(builder.build());
                this.kp = kpg.genKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
    }

    public PublicKey getPublicKey(){
        try {
            keyStore = KeyStore.getInstance(provider);
            keyStore.load(null);
            entry = keyStore.getEntry(alias, null);
            publicKey = keyStore.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        try {
            keyStore = KeyStore.getInstance(provider);
            keyStore.load(null);
            entry = keyStore.getEntry(alias, null);
            privateKey = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public String chiffer(String plain){
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            encryptedBytes = cipher.doFinal(plain.getBytes());
            System.out.println("Chiffré  ? :" + new String(encryptedBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedBytes.toString();
    }

    public String dechiffer(){
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            decryptedBytes = cipher.doFinal(encryptedBytes);
            System.out.println("Chiffré  ? :" + new String(decryptedBytes));
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return new String(decryptedBytes);
    }




}
