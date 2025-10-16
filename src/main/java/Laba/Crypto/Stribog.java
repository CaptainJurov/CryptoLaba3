package Laba.Crypto;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Stribog implements SignatureAlgorithm {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Override
    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECGOST3410-2012", "BC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("Tc26-Gost-3410-12-512-paramSetA");
        keyGen.initialize(ecSpec, new SecureRandom());

        java.security.KeyPair pair = keyGen.generateKeyPair();
        return new KeyPair(pair.getPrivate(), pair.getPublic());
    }

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("ECGOST3410-2012-512", "BC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    @Override
    public boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("ECGOST3410-2012-512", "BC");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}