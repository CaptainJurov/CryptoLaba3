package Laba.Crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPair {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public KeyPair(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
