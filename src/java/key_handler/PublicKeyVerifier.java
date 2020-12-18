package key_handler;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;
import utils.ProjectConstants;

public class PublicKeyVerifier {
    
    private static final Pattern NO_WHITESPACES = Pattern.compile("[\\s]+", Pattern.DOTALL | Pattern.MULTILINE);
    private RSAPublicKey publicKey;
    
    public RSAPublicKey loadPublicKey (String files_str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (this.publicKey != null)
            return this.publicKey;
        
        String content = String.join("", files_str);

        // remove noise
        content = content.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");
        
        content = NO_WHITESPACES.matcher(content).replaceAll("");

        byte[] pubKeyBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey temp = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return (this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec));
    }
    
    public boolean isVerified(byte[] signature2verify) 
            throws InvalidKeyException, SignatureException, IllegalStateException, NoSuchAlgorithmException {
        
        Signature signature = Signature.getInstance(ProjectConstants.SIGNATURE);
        
        if (this.publicKey == null)
            throw new IllegalStateException("The public key should be loaded first!");
        
        signature.initVerify(this.publicKey);
        signature.update(signature2verify);
        
        return signature.verify(signature2verify);
    }
    
}
