package key_handler;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class PrivateKeySigner {
    private static final Pattern NO_WHITESPACES = Pattern.compile("[\\s]+", Pattern.DOTALL | Pattern.MULTILINE);
    private RSAPrivateKey privateKey;

    public PrivateKeySigner() { }

    public RSAPrivateKey loadPrivateKey(File fromFile, String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        if (this.privateKey != null)
            return this.privateKey;

        // read content file
        List<String> lines = Files.readAllLines(fromFile.toPath());
        String content = String.join("", lines);

        // remove noise
        content = content.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "")
                .replace("-----END ENCRYPTED PRIVATE KEY-----", "");
        content = PrivateKeySigner.NO_WHITESPACES.matcher(content)
                .replaceAll("");

        // get as bytes
        byte[] privKeyBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));

        if (passphrase != null)
            privKeyBytes = this.decryptBytes(privKeyBytes, passphrase);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes, "RSA");
        return (this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec));
    }
    
    public RSAPrivateKey loadPrivateKey(String files_str, String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        if (this.privateKey != null)
            return this.privateKey;
        
        String content = String.join("", files_str);

        // remove noise
        content = content.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "")
                .replace("-----END ENCRYPTED PRIVATE KEY-----", "");
        content = PrivateKeySigner.NO_WHITESPACES.matcher(content)
                .replaceAll("");

        // get as bytes
        byte[] privKeyBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));

        if (passphrase != null)
            privKeyBytes = this.decryptBytes(privKeyBytes, passphrase);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes, "RSA");
        return (this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec));
    }

    public byte[] sign(File file) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, IllegalStateException {
        if (this.privateKey == null)
            throw new IllegalStateException("The private key should be loaded first!");

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(this.privateKey);
        signer.update(Files.readAllBytes(file.toPath()));
        return signer.sign();
    }

    private byte[] decryptBytes(byte[] encryptedBytes, String passphrase) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        EncryptedPrivateKeyInfo encryptedKey = new EncryptedPrivateKeyInfo(encryptedBytes);

        // get secret key from passphrase
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(encryptedKey.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // decrypt the key
        Cipher cipher = Cipher.getInstance(encryptedKey.getAlgName());
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, encryptedKey.getAlgParameters());
        return cipher.doFinal(encryptedKey.getEncryptedData());
    }
}