package servlets;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import static javax.crypto.Cipher.PRIVATE_KEY;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import objects.Person;
//Optional 


@WebServlet(name="SignDocument", urlPatterns = {"/SignDocument"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 2, // 2MB
    maxRequestSize = 1024 * 1024 * 2 + 1024 // 2.1 MB 
)
public class SignDocument extends HttpServlet {
    
    private String PK_EXTENSION = ".asc";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        
        if (!ServletFileUpload.isMultipartContent(request)){
            response.setStatus(400);
            return;
        }
        
        try {            
            String name = request.getParameter("name");
            String lastname = request.getParameter("lastname");
            String identifier_str = request.getParameter("identifier");
            String age_str = request.getParameter("age");
            
            
            Person person = ValidateData.createPerson(name, lastname, identifier_str, age_str);
            if(person == null){
                response.setStatus(400);
                return;
            }
            
            String passphrase = request.getParameter("passphrase");
            Part filePart = request.getPart("file");
            String file_name = filePart.getSubmittedFileName().replace(PK_EXTENSION, "");
            
            //File file = PDF.createPDF(person, file_name);
            PDF.createPDF(person, file_name);
            /*if(file == null){
                response.setStatus(500);
                return;
            }*/
            
            String private_key = Streams.asString(filePart.getInputStream(), "UTF-8");
            
            try {
                String path = System.getProperty("user.dir").concat("/").concat(file_name).concat(".pdf");
                byte data[] = readFile(path);
                
                Signature signature = Signature.getInstance("NONEwithRSA");
                PrivateKey privateKey = KeyFromString.loadPrivateKey(private_key);
                signature.initSign(privateKey);
                signature.update(data);
                
                byte signed[] = signature.sign();
                saveSignedDocument(path, signed);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.err.println(Arrays.toString(ex.getStackTrace()));
                response.setStatus(500);
            }
            
        } catch (IOException | ServletException ex) {
            System.err.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
            response.setStatus(500);
        } finally {
            PrintWriter out = response.getWriter();
            out.close();
        }
        
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private byte[] readFile(String path) throws Exception {
        return Files.readAllBytes(Paths.get(path));
    }
    
    public void saveSignedDocument(String path, byte[] sign) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)){
            fos.write(sign);
        }
    }
    
    private static class KeyFromString {
        public static PrivateKey loadPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
            StringBuilder pkcs8Lines = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new StringReader(privateKeyStr));
            String line;
            while ((line = rdr.readLine()) != null) {
                pkcs8Lines.append(line);
            }

            // Remove the "BEGIN" and "END" lines, as well as any whitespace

            String pkcs8Pem = pkcs8Lines.toString();
            pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replaceAll("\\s+","");

            // Base64 decode the result

            byte [] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem.getBytes());

            // extract the private key

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            
            System.out.println(privKey);
            return privKey;
        }
        
        public static PrivateKey loadPrivateKey(String privateKeyStr, String passcode) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
            StringBuilder pkcs8Lines = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new StringReader(privateKeyStr));
            String line;
            while ((line = rdr.readLine()) != null) {
                pkcs8Lines.append(line);
            }
            
            Cipher cipher;

            // Remove the "BEGIN" and "END" lines, as well as any whitespace
            String pkcs8Pem = pkcs8Lines.toString();
            pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replaceAll("\\s+","");
            // Base64 decode the result

            byte [] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem.getBytes());

            // extract the private key

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            
            System.out.println(privKey);
            return privKey;
        }
    }
    
    
    private static class ValidateData{
        public static Person createPerson(String name, String lastname, String identifier_str, String age_str){
            Person person = null;
            Pattern pattern = Pattern.compile("^([a-zA-ZñÑáéíóúüÁÉÍÓÚÜ]{2,15}( )?){1,2}$");
            Matcher matcher = pattern.matcher(name);
            if(!matcher.matches())
                return null;
            matcher = pattern.matcher(lastname);
            if(!matcher.matches())
                return null;
            try {
                long identifier = Long.parseLong(identifier_str);
                byte age = Byte.parseByte(age_str);
                
                person = new Person(name, lastname, identifier, age);
            } catch(NumberFormatException e) {
                System.out.println(e.toString());
            }
            
            return person;
        }
    }
    
    private static class PDF {
        public static File createPDF(Person person, String file_name) {
            try {
                Document document = new Document();
                String path = System.getProperty("user.dir").concat("/").concat(file_name).concat(".pdf");
                File file = new File(path);
                //System.out.println(path);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                Font font_center = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Element.ALIGN_CENTER, BaseColor.BLACK);
                Font font_left = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Element.ALIGN_LEFT, BaseColor.BLACK);
                Chunk line_separator = new Chunk(new LineSeparator(font_left));
                document.add(new Chunk(person.getCompleteName(), font_center));
                document.add(new Phrase("\n"));
                document.add(line_separator);
                document.add(Chunk.NEWLINE);
                document.add(new Chunk("\nBoleta: ".concat(String.valueOf(person.getIdentifier())), font_left));
                document.add(Chunk.NEWLINE);
                document.add(new Chunk("\nEdad: ".concat(String.valueOf(person.getAge())), font_left));
                document.add(Chunk.NEWLINE);
                
                document.close();
                
                return file;
                
            } catch(DocumentException | FileNotFoundException e) {
                
            }
            
            return null;
        }
        
    }

}