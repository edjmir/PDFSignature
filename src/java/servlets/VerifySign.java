package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import key_handler.PublicKeyVerifier;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;


@WebServlet(name="VerifySign", urlPatterns = {"/VerifySign"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 10 + 1024 // 10.1 MB 
)
public class VerifySign extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        if (!ServletFileUpload.isMultipartContent(request)){
            response.setStatus(400);
            return;
        }
        
        Part pdf_part = request.getPart("pdf_file");
        Part public_key_part = request.getPart("public_key");
        Part signature_part = request.getPart("signature_file");
        
        String public_key = Streams.asString(public_key_part.getInputStream(), "UTF-8");
        if("undefined".equals(public_key)){
            response.setStatus(400);
            out.print("{\"warning\" : \"Invalid data\"}");
            return;
        }
        
        try {
            PublicKeyVerifier pkv = new PublicKeyVerifier();
            pkv.loadPublicKey(public_key);
            
            byte[] pdf_bytes = pdf_part.getInputStream().readAllBytes();
            byte[] signature_bytes = Base64.getDecoder().decode(signature_part.getInputStream().readAllBytes());
            
            if(signature_bytes.length != 512 || pdf_bytes.length < 1){
                response.setStatus(400);
                out.print("{\"warning\" : \"Invalid data\"}");
                return;
            }
            
            boolean is_verified = pkv.isVerified(signature_bytes, pdf_bytes);
            response.setStatus(200);
            out.print("{\"verified\" : " + is_verified + "}");
            
        } catch (IllegalStateException | NoSuchAlgorithmException ex) {
            System.err.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
            response.setStatus(500);
            out.print("{\"error\" : true}");
        } catch(Exception ex){
            System.err.println(ex.getMessage());
            System.err.println(Arrays.toString(ex.getStackTrace()));
            response.setStatus(400);
            out.print("{\"warning\" : \"Invalid data\"}");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
         * Parses a PDF to a plain text file.
         * @param pdf_path the original PDF
         * @return signature
         * @throws IOException
         */
        /*public static String parsePdf(InputStream pdf_path) throws IOException {
            final Pattern NO_WHITESPACES = Pattern.compile("[\\s]+", Pattern.DOTALL | Pattern.MULTILINE);
            
            PdfReader reader = new PdfReader(pdf_path);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            
            StringBuilder stringBuilder = new StringBuilder(600);
            TextExtractionStrategy strategy;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                stringBuilder.append(strategy.getResultantText());
            }
            reader.close();
            String signature = stringBuilder.toString();
            signature = NO_WHITESPACES.matcher(signature).replaceAll("");
            return signature.substring(0, 684);
        }*/
}
