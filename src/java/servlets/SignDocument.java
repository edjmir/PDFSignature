package servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Part;
import key_handler.PrivateKeySigner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import utils.FileManagement;
import utils.ProjectConstants;

@WebServlet(name="SignDocument", urlPatterns = {"/SignDocument"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 10 + 1024 // 10.1 MB 
)
public class SignDocument extends HttpServlet {

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
            String comment = request.getParameter("comment");
            
            Person person = ValidateData.createPerson(name, lastname, identifier_str, age_str, comment);
            if(person == null){
                response.setStatus(400);
                return;
            }
            
            String passphrase = request.getParameter("passphrase");
            Part filePart = request.getPart("file");
            //String file_name = filePart.getSubmittedFileName().replaceFirst("\\.[a-zA-Z]+$", "");
            String pdf_path = ProjectConstants.PDF_FILE_DEFAULT_PATH
                .concat(String.valueOf(System.currentTimeMillis()))
                .concat("-")
                .concat(ProjectConstants.PDF_FILE_DEFAULT_NAME);
            
            String signature_path = ProjectConstants.PDF_FILE_DEFAULT_PATH
                .concat(String.valueOf(System.currentTimeMillis()))
                .concat("-")
                .concat(ProjectConstants.PDF_SIGNATURE_DEFAULT_NAME);
            
            String private_key = Streams.asString(filePart.getInputStream(), "UTF-8");
            File pdf_file = PDF.createPDF(person, pdf_path);
            
            PrivateKeySigner signer = new PrivateKeySigner();
            
            if("null".equals(String.valueOf(passphrase)))
                signer.loadPrivateKey(private_key, null);
            else
                signer.loadPrivateKey(private_key, passphrase);
            
            byte[] signature = Base64.getEncoder().encode(signer.sign(pdf_file));
            System.out.println(new String(signature));
            
            
            final String zip_path = ProjectConstants.ZIP_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat(ProjectConstants.ZIP_PDF_FILE_DEFAULT_NAME);
            
            try{
                FileManagement fileManagement = new FileManagement();
                File signature_file = fileManagement.createFile(signature_path, false, signature);
                fileManagement.createZip(zip_path, pdf_file, signature_file);
                fileManagement.deleteFiles(pdf_file);
            }catch(IOException e){
                response.setStatus(500);
                return;
            }
            
            request.setAttribute("file_path", zip_path);
            request.setAttribute("file_name", ProjectConstants.PDF_FILE_DEFAULT_NAME);
            
            request.getRequestDispatcher("DownloadFile").forward(request, response);            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
            response.setStatus(500);
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
    
    private static class ValidateData{
        public static Person createPerson(String name, String lastname, String identifier_str, String age_str, String comment){
            if(name == null || lastname == null || identifier_str == null || age_str == null)
                return null;
            
            Person person = null;
            Pattern name_pattern = Pattern.compile("^([a-zA-ZñÑáéíóúüÁÉÍÓÚÜ]{2,15}( )?){1,2}$");
            Pattern identifier_pattern = Pattern.compile("[0-9]{10}");
            Matcher matcher = name_pattern.matcher(name);
            if(!matcher.matches())
                return null;
            matcher = name_pattern.matcher(lastname);
            if(!matcher.matches())
                return null;
            matcher = identifier_pattern.matcher(identifier_str);
            if(!matcher.matches())
                return null;
            try {
                long identifier = Long.parseLong(identifier_str);
                byte age = Byte.parseByte(age_str);
                
                person = new Person(name, lastname, identifier, age, comment);
            } catch(NumberFormatException e) {
                System.err.println(e.getMessage());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
            
            return person;
        }
    }
    
    private static class Person {
    
        private final String name;
        private final String lastname;
        private final String comment;
        private final long identifier;
        private final byte age;

        public Person(String name, String lastname, long identifier, byte age, String comment) {
            this.name = name;
            this.lastname = lastname;
            this.identifier = identifier;
            this.age = age;
            this.comment = comment;
        }

        public String getCompleteName() {
            return name + ' ' + lastname;
        }
        
        public String getComment() {
            return this.comment;
        }

        public long getIdentifier() {
            return identifier;
        }

        public byte getAge() {
            return age;
        }

    }
    
    private static class PDF {
        
        private static final PDFont FONT = PDType1Font.HELVETICA;
        private static final float FONT_SIZE = 12;
        private static final float LEADING = -1.5f * FONT_SIZE;
        
        public static File createPDF(Person person, String path) {
            try (PDDocument document = new PDDocument()){
                
                File file = new File(path);
                PDPage page = new PDPage();
                document.addPage(page);
                
                PDFont font = PDType1Font.TIMES_ROMAN;
                PDRectangle mediaBox = page.getMediaBox();
                float marginY = 80;
                float marginX = 60;
                float width = mediaBox.getWidth() - 2 * marginX;
                float startX = mediaBox.getLowerLeftX() + marginX;
                float startY = mediaBox.getUpperRightY() - marginY;
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    addParagraph(contentStream, width, startX, startY, person.getCompleteName(), true);
                    addParagraph(contentStream, width, 0, -FONT_SIZE, "Edad: " + person.getAge());
                    addParagraph(contentStream, width, 0, -FONT_SIZE, "Boleta: " + person.getIdentifier());
                    if(person.getComment() != null)
                        addParagraph(contentStream, width, 0, -FONT_SIZE, "Comentario: " + person.getComment());
                    contentStream.endText();
                }
                
                document.save(file);
                
                return file;
                
            } catch(Exception e) {
                System.err.println(e.getMessage());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
            return null;
        }
        
        private static void addParagraph(PDPageContentStream contentStream, float width, float sx, float sy, String text)
                throws IOException {
            addParagraph(contentStream, width, sx, sy, text, false);
        }

        private static void addParagraph(PDPageContentStream contentStream, float width, float sx,
                                          float sy, String text, boolean justify) throws IOException {
            List<String> lines = parseLines(text, width);
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.newLineAtOffset(sx, sy);
            for (String line: lines) {
                float charSpacing = 0;
                if (justify){
                    if (line.length() > 1) {
                        float size = FONT_SIZE * FONT.getStringWidth(line) / 1000;
                        float free = width - size;
                        if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                            charSpacing = free / (line.length() - 1);
                        }
                    }
                }
                contentStream.setCharacterSpacing(charSpacing);
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, LEADING);
            }
        }

        private static List<String> parseLines(String text, float width) throws IOException {
            List<String> lines = new ArrayList<String>();
            int lastSpace = -1;
            while (text.length() > 0) {
                int spaceIndex = text.indexOf(' ', lastSpace + 1);
                if (spaceIndex < 0)
                    spaceIndex = text.length();
                String subString = text.substring(0, spaceIndex);
                float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
                if (size > width) {
                    if (lastSpace < 0){
                        lastSpace = spaceIndex;
                    }
                    subString = text.substring(0, lastSpace);
                    lines.add(subString);
                    text = text.substring(lastSpace).trim();
                    lastSpace = -1;
                } else if (spaceIndex == text.length()) {
                    lines.add(text);
                    text = "";
                } else {
                    lastSpace = spaceIndex;
                }
            }
            return lines;
        }
        
    }
}