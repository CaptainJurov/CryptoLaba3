package Laba.Crypto;


import java.security.PrivateKey;
import java.security.PublicKey;


public interface SignatureAlgorithm {
    byte[] sign(byte[] data, PrivateKey privateKey) throws Exception;
    boolean verify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception;
    KeyPair generateKeyPair() throws Exception;
}
