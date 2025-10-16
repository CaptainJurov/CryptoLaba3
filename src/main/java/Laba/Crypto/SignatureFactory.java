package Laba.Crypto;

public class SignatureFactory {

    public enum Algorithm {
        EL_GAMAL,
        RSA,
        GOST
    }

    public static SignatureAlgorithm createAlgorithm(Algorithm algorithm) {
        switch (algorithm) {
            case EL_GAMAL:
                return new ElGamal();
            case RSA:
                return new RSA();
            case GOST:
                return new Stribog();
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
