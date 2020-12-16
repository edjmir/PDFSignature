package key_handler;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;
import utils.ProjectConstants;

public class KeyPairGen {
    private final String PRIVKEY_ENCRYPTION_ALGORITHM;
    private final String KEY_ALGORITHM;
    private final short KEY_SIZE;

    private KeyPair keyPair;

    /**
     * Instantiates this class and sets the given values
     *
     * @param privateKeyEncAlgorithm
     * 	the encryption algorithm used to encrypt symmetrically the private key
     * @param keyAlgorithm
     * 	the algorithm used to create the key pair
     * @param key_size
     * 	the size of the created keys
     */
    public KeyPairGen(String privateKeyEncAlgorithm, String keyAlgorithm, short key_size) {
        this.PRIVKEY_ENCRYPTION_ALGORITHM = privateKeyEncAlgorithm;
        this.KEY_ALGORITHM = keyAlgorithm;
        this.KEY_SIZE = key_size;
    }

    /**
     * Instantiates this class with some default values
     * using AES to encrypt the private key
     * RSA as the algorithm to be used to create the key pair
     * 4096 bits for the key size
     */
    public KeyPairGen() {
        this.PRIVKEY_ENCRYPTION_ALGORITHM = ProjectConstants.PRIVKEY_ENCRYPTION_DEFAULT_ALGORITHM;
        this.KEY_ALGORITHM = ProjectConstants.KEY_DEFAULT_ALGORITHM;
        this.KEY_SIZE = ProjectConstants.KEY_DEFAULT_SIZE;
    }

    /**
     * Encrypts the given private key with the selected algorithm
     *
     * @param privateKey
     * 	the private key to be encrypted
     * @param passphrase
     * 	the passphrase for the encrypted private key
     * @param salt
     * 	the salt, see {@link #randomSalt()}
     * @param iv
     * 	the initialization vector, see {@link #randomIV()}
     *
     * @return encrypted private key info
     */
    private EncryptedPrivateKeyInfo encryptPrivateKey(PrivateKey privateKey, String passphrase, byte[] salt, IvParameterSpec iv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidParameterSpecException {
        byte[] plainBytes = privateKey.getEncoded();

        // get the secret key from the passphrase
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 65_536, iv);

        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(this.PRIVKEY_ENCRYPTION_ALGORITHM);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(this.PRIVKEY_ENCRYPTION_ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        byte[] encryptedBytes = cipher.doFinal(plainBytes);

        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(this.PRIVKEY_ENCRYPTION_ALGORITHM);
        algorithmParameters.init(pbeParamSpec);
        return new EncryptedPrivateKeyInfo(algorithmParameters, encryptedBytes);
    }

    /**
     * Generates a random initialization vector
     *
     * @return the generated initialization vector
     */
    private IvParameterSpec randomIV() {
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        return new IvParameterSpec(ivBytes);
    }

    /**
     * Generates some random salt
     *
     * @return the generated random salt
     */
    private byte[] randomSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Creates a key pair with the specified algorithm and key size
     *
     * @return the created key pair
     * @throws java.security.NoSuchAlgorithmException
     */
    public KeyPair genKeyPair() throws NoSuchAlgorithmException {
        if (this.keyPair != null)
                return this.keyPair;

        // create private key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstanceStrong();//.getInstance("SHA1PRNG");
        keyPairGenerator.initialize(this.KEY_SIZE, random);

        return (this.keyPair = keyPairGenerator.generateKeyPair());
    }

    public EncryptedPrivateKeyPair createAndSaveKeys(File file, String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, InvalidParameterSpecException, InvalidKeySpecException, IllegalBlockSizeException {
        KeyPair keyPair = this.genKeyPair();
        PrivateKey privKey = keyPair.getPrivate();
        
        //System.out.println("Pass " + passphrase == null);
        boolean encrypt = !(passphrase == null);
        //System.out.println(encrypt + "Wow" + this.KEY_ALGORITHM);

        EncryptedPrivateKeyInfo encryptedPrivateKey = encrypt ? this.encryptPrivateKey(
            privKey,
            passphrase,
            this.randomSalt(),
            this.randomIV()
        ) : null;

        // encode bytes to Base 64
        String privKeyEncrypted = Base64.getEncoder().encodeToString(
            encrypt ? encryptedPrivateKey.getEncoded() : privKey.getEncoded()
        );

        // dump base64 data to a string builder
        StringBuilder builder = new StringBuilder(privKeyEncrypted.length() + 60 /* for the BEGIN/END lines*/);

        // add header
        builder.append("-----BEGIN ");
        builder.append(encrypt ? "ENCRYPTED" : this.KEY_ALGORITHM);
        builder.append(" PRIVATE KEY-----\n");

        // split the string in chunks of 64 chars
        int i = 0;
        int next_i;
        int length = privKeyEncrypted.length();
        while (i < length) {
                next_i = 64 + i;
                builder.append(privKeyEncrypted, i, Math.min(next_i, length)).append('\n');
                i += 64;
        }

        // add footer
        builder.append("-----END ");
        builder.append(encrypt ? "ENCRYPTED" : this.KEY_ALGORITHM);
        builder.append(" PRIVATE KEY-----\n");

        // write to file
        try (FileOutputStream outStream = new FileOutputStream(file)) {
                outStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        }

        //System.out.println(builder.toString());
        return new EncryptedPrivateKeyPair(keyPair, encryptedPrivateKey);
    }

    public EncryptedPrivateKeyPair createAndSaveKeys(File file) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, InvalidParameterSpecException, InvalidKeySpecException, IllegalBlockSizeException {
        return this.createAndSaveKeys(file, null);
    }

    public static class EncryptedPrivateKeyPair {
        private final KeyPair keyPair;
        private final EncryptedPrivateKeyInfo encryptedPrivateKey;

        public EncryptedPrivateKeyPair(KeyPair keyPair, EncryptedPrivateKeyInfo encryptedPrivateKey) {
            this.keyPair = keyPair;
            this.encryptedPrivateKey = encryptedPrivateKey;
        }

        public EncryptedPrivateKeyPair(KeyPair keyPair) {
                this(keyPair, null);
        }

        /**
         * Returns a reference to the public key component of this key pair.
         *
         * @return a reference to the public key.
         */
        public PublicKey getPublic() {
            return this.keyPair.getPublic();
        }

        /**
         * Returns a reference to the private key component of this key pair.
         *
         * @return a reference to the private key.
         */
        public PrivateKey getPrivate() {
            return this.keyPair.getPrivate();
        }

        /**
         * Returns a reference to the ENCRYPTED private key component of this key pair.
         *
         * @return a reference to the ENCRYPTED private key. This method can return null if the private key was
         * not encrypted
         */
        public EncryptedPrivateKeyInfo getEncryptedPrivateKey() {
            return this.encryptedPrivateKey;
        }
    }
}
