package KeyGenerator;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class KeyHandler {
    
    public KeyPair generateKeyPair(String passcode){
        final short KEY_LENGTH = 4096;
        try {
            //Generating keypairs
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_LENGTH);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            
            byte[] encoded_pk = keyPair.getPrivate().getEncoded();
            
            final String ALGORITHM = "AES";

            int count = 20;// hash iteration count
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passcode.toCharArray());
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
            
            
            /*SecureRandom secureRandom = new SecureRandom(passcode.getBytes("UTF-8"));
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, secureRandom);
            
            SecretKey secretKey = keyGenerator.generateKey();
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            keyPairGenerator.initialize(KEY_LENGTH);
            
            byte[] pk = cipher.doFinal(keyPairGenerator.genKeyPair().getPrivate().getEncoded());
            byte[] pubk = cipher.doFinal(keyPairGenerator.genKeyPair().getPublic().getEncoded());
            
            return keyPairGenerator.generateKeyPair();*/
        } catch (Exception ex) { 
            System.out.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }
    public KeyPair generateKeyPair(){
        final short KEY_LENGTH = 4096;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_LENGTH);            
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) { 
            System.out.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }   
}

/*

Cipher pbeCipher = Cipher.getInstance(MYPBEALG);

// Initialize PBE Cipher with key and parameters
pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

// Encrypt the encoded Private Key with the PBE key
byte[] ciphertext = pbeCipher.doFinal(encodedprivkey);

// Now construct  PKCS #8 EncryptedPrivateKeyInfo object
AlgorithmParameters algparms = AlgorithmParameters.getInstance(MYPBEALG);
algparms.init(pbeParamSpec);
EncryptedPrivateKeyInfo encinfo = new EncryptedPrivateKeyInfo(algparms, ciphertext);

// and here we have it! a DER encoded PKCS#8 encrypted key!
byte[] encryptedPkcs8 = encinfo.getEncoded();
*/