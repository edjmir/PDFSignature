package utils;

public class ProjectConstants {
    
    public static final String PRIVATE_KEY_FILE_DEFAULT_NAME = "private_key.pem";
    public static final String PRIVATE_KEY_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\");
    public static final String PDF_FILE_DEFAULT_PATH =
        System.getProperty("user.dir").concat("\\"); //PDF\\
    public static final String PDF_FILE_DEFAULT_NAME = "MyPdf.pdf";
    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    public static final String PRIVKEY_ENCRYPTION_DEFAULT_ALGORITHM = "PBEWithSHA1AndDESede";
    public static final String KEY_DEFAULT_ALGORITHM = "RSA";
    public static final short KEY_DEFAULT_SIZE = 4096;
    
}
