package utils;

public class ProjectConstants {
    
    //KEY PROPERTIES
    public static final String PRIVKEY_ENCRYPTION_DEFAULT_ALGORITHM = "PBEWithSHA1AndDESede";
    public static final String KEY_DEFAULT_ALGORITHM = "RSA";
    public static final short KEY_DEFAULT_SIZE = 4096;
    public static final String SIGNATURE = "SHA256withRSA";
    
    //KEY STORAGE
        //PRIVATE KEY
    public static final String PRIVATE_KEY_FILE_DEFAULT_NAME = "private_key.pem";
    public static final String PRIVATE_KEY_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\");
        //PUBLIC KEY
    public static final String PUBLIC_KEY_FILE_DEFAULT_NAME = "public_key.pem";
    public static final String PUBLIC_KEY_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\");
    
    //ZIP
    public static final String ZIP_KEYS_FILE_DEFAULT_NAME = "keys.zip";
    public static final String ZIP_PDF_FILE_DEFAULT_NAME = "pdf.zip";
    public static final String ZIP_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\");
    
    //PDF PROPERTIES
    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    
    //FILE STORAGE
    public static final String PDF_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\"); //PDF\\
    public static final String PDF_FILE_DEFAULT_NAME = "MyPdf.pdf";
    public static final String PDF_SIGNATURE_DEFAULT_NAME = "signature.pem";
    public static final short BUFFER_SIZE = 4096;
    
}
