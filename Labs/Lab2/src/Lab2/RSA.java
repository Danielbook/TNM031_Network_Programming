package Lab2;

import java.math.BigInteger;
import java.util.Random;


//    // to convert an integer b into a BigInteger
//    int b = 170;
//    BigInteger bigB = new BigInteger(String.valueOf(b));
//
//
//    // 	to read a string from keyboard
//    String input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
//
//    // 	to convert a string s into a BigInteger
//    String s = "abc";
//    BigInteger c = new BigInteger(s.getBytes());
//
//
//    // to convert a BigInteger back to a string
//    BigInteger a;
//    String s = new String(a.toByteArray());

public class RSA {
    // Keyes used in encryption and decryption
    private BigInteger d,e,n;

    /**
     * Generates a large prime used for the keys
     * @return A large random prime number.
     */
    private BigInteger genLargePrime(){
        final int BIT_LENGTH = 2048;
        return BigInteger.probablePrime(BIT_LENGTH, new Random());
    }

    /**
     * Creates a session for encrypting and decrypting messages with the RSA-algorithm
     */
    public RSA() {
        // p,q  and e is large random primes. n is the factor of p and q.
        BigInteger p = genLargePrime();
        BigInteger q = genLargePrime();
        n = p.multiply(q);

        e = genLargePrime();

        // phi = (p-1)(q-1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // (e*d % p*q = 1)
        d = e.modInverse(phi);
    }

    /**
     * Getter for d variable
     * @return d
     */
    public BigInteger getD() {
        return d;
    }

    /**
     * Getter for the exponent e, one of the public keys
     * @return e
     */
    public BigInteger getE() {
        return e;
    }

    /**
     * Getter for the variable n, one of the public keys
     * @return n
     */
    public BigInteger getN() {
        return n;
    }

    /**
     * Encrypts the message.
     * @param message   Message to encrypt
     * @param exponent  public key: e variable
     * @param product   public key: n variable
     * @return Encrypted message
     */
    public static BigInteger encryptMessage(String message, BigInteger exponent, BigInteger product) {
        // m = c^d
        BigInteger c = new BigInteger(message.getBytes());
        return c.modPow(exponent, product);
    }

    /**
     * Decryptes the message in the encrypted BigInteger c
     * @param c The encrypted message
     * @return Decrypted message
     */
    public String decryptMessage(BigInteger c) {
        BigInteger m = c.modPow(this.d, this.n);
        // Convert the BigInteger back to a string
        return new String( m.toByteArray() );
    }


}
