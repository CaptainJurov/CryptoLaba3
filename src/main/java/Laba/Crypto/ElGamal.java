package Laba.Crypto;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class ElGamal implements SignatureAlgorithm {

    private static final int KEY_SIZE = 512;
    private final SecureRandom random = new SecureRandom();

    private BigInteger p;
    private BigInteger g;

    public ElGamal() {
        generateParameters();
    }

    private void generateParameters() {
        p = BigInteger.probablePrime(KEY_SIZE, random);
        g = new BigInteger("2");
    }

    @Override
    public KeyPair generateKeyPair() throws Exception {
        BigInteger x = new BigInteger(KEY_SIZE - 1, random);
        BigInteger y = g.modPow(x, p);

        return new KeyPair(
                new ElGamalPrivateKey(x, p, g),
                new ElGamalPublicKey(y, p, g)
        );
    }

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        if (!(privateKey instanceof ElGamalPrivateKey)) {
            throw new IllegalArgumentException("Invalid private key type");
        }

        ElGamalPrivateKey priv = (ElGamalPrivateKey) privateKey;
        byte[] hash = HashUtils.computeSHA256(data);
        BigInteger h = new BigInteger(1, hash);

        BigInteger p = priv.getP();
        BigInteger g = priv.getG();
        BigInteger x = priv.getX();

        BigInteger k;
        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        do {
            k = new BigInteger(p.bitLength() - 1, random);
        } while (!k.gcd(pMinusOne).equals(BigInteger.ONE));

        BigInteger r = g.modPow(k, p);

        BigInteger s = h.subtract(x.multiply(r))
                .multiply(k.modInverse(pMinusOne))
                .mod(pMinusOne);

        byte[] rBytes = r.toByteArray();
        byte[] sBytes = s.toByteArray();

        byte[] signature = new byte[rBytes.length + sBytes.length + 8];
        System.arraycopy(intToBytes(rBytes.length), 0, signature, 0, 4);
        System.arraycopy(rBytes, 0, signature, 4, rBytes.length);
        System.arraycopy(intToBytes(sBytes.length), 0, signature, 4 + rBytes.length, 4);
        System.arraycopy(sBytes, 0, signature, 8 + rBytes.length, sBytes.length);

        return signature;
    }

    @Override
    public boolean verify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        if (!(publicKey instanceof ElGamalPublicKey)) {
            throw new IllegalArgumentException("Invalid public key type");
        }

        ElGamalPublicKey pub = (ElGamalPublicKey) publicKey;

        int rLength = bytesToInt(signature, 0);
        int sLength = bytesToInt(signature, 4 + rLength);

        BigInteger r = new BigInteger(1, java.util.Arrays.copyOfRange(signature, 4, 4 + rLength));
        BigInteger s = new BigInteger(1, java.util.Arrays.copyOfRange(signature, 8 + rLength, 8 + rLength + sLength));

        BigInteger p = pub.getP();
        BigInteger g = pub.getG();
        BigInteger y = pub.getY();

        byte[] hash = HashUtils.computeSHA256(data);
        BigInteger h = new BigInteger(1, hash);

        BigInteger left = g.modPow(h, p);
        BigInteger right = y.modPow(r, p).multiply(r.modPow(s, p)).mod(p);

        return left.equals(right);
    }

    private byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    private int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }
}


class ElGamalPrivateKey implements PrivateKey {
    private final BigInteger x;
    private final BigInteger p;
    private final BigInteger g;

    public ElGamalPrivateKey(BigInteger x, BigInteger p, BigInteger g) {
        this.x = x;
        this.p = p;
        this.g = g;
    }

    public BigInteger getX() { return x; }
    public BigInteger getP() { return p; }
    public BigInteger getG() { return g; }

    @Override public String getAlgorithm() { return "ElGamal"; }
    @Override public String getFormat() { return "RAW"; }
    @Override public byte[] getEncoded() { return x.toByteArray(); }
}

class ElGamalPublicKey implements PublicKey {
    private final BigInteger y;
    private final BigInteger p;
    private final BigInteger g;

    public ElGamalPublicKey(BigInteger y, BigInteger p, BigInteger g) {
        this.y = y;
        this.p = p;
        this.g = g;
    }

    public BigInteger getY() { return y; }
    public BigInteger getP() { return p; }
    public BigInteger getG() { return g; }

    @Override public String getAlgorithm() { return "ElGamal"; }
    @Override public String getFormat() { return "RAW"; }
    @Override public byte[] getEncoded() { return y.toByteArray(); }
}