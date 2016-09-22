package Lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * Created by Daniel on 2016-09-15.
 */
public class Test {
    public static void main(String[] args) {
        RSA encryptSession = new RSA();

        System.out.println("\nEnter message to encrypt: ");
        String input = "";

        try {
            input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
        } catch(IOException e) {
            System.out.println("Something went wrong: " + e.toString());
            return;
        }

        // Get public keys e and n
        BigInteger e = encryptSession.getE();
        BigInteger n = encryptSession.getN();

        // Encrypt the message
        BigInteger encrypted = RSA.encryptMessage(input, e, n);

        // Present encrypted message
        System.out.println("Encrypted message: " + encrypted);

        // Present the decrypted message
        String decryptedMessage = encryptSession.decryptMessage(encrypted);
        System.out.println("Decrypted message: " + decryptedMessage);
    }
}
