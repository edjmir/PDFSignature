package key_handler;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;
import utils.Hex;
import utils.ProjectConstants;

public class PublicKeyVerifier {
    
    private static final Pattern NO_WHITESPACES = Pattern.compile("[\\s]+", Pattern.DOTALL | Pattern.MULTILINE);
    private RSAPublicKey publicKey;
    
    public RSAPublicKey loadPublicKey (String files_str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (this.publicKey != null)
            return this.publicKey;
        
        String content = String.join("", files_str);

        // remove noise
        content = content.replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "");
        
        content = NO_WHITESPACES.matcher(content).replaceAll("");

        // get as bytes
        //String decoded = new String(Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        byte[] pubKeyBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));// Hex.decodeHex(decoded.toCharArray());// Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec));
    }
    
    public boolean isVerified(Signature signature, byte[] data2verify) 
            throws InvalidKeyException, SignatureException, IllegalStateException {
        
        if (this.publicKey == null)
            throw new IllegalStateException("The public key should be loaded first!");
        
        signature.initVerify(this.publicKey);
        return signature.verify(data2verify);
    }
    
}
